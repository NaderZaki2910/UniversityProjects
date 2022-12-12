//Imports
require('dotenv').config()
const express = require("express");
const Web3 = require("web3");
const Provider = require("@truffle/hdwallet-provider");
const contract_abi = require("./abi");
const aesjs = require("aes-js");
const sigUtil = require("@metamask/eth-sig-util");
const cors = require("cors");
const utf8 = require("utf8");
// Express
const app = express();
app.use(cors());
app.use(express.json());
const port = 5000;
// Server keys
const serverPublicKey = process.env.serverPublicKey;
const serverPrivateKey = process.env.serverPrivateKey
// Other keys
const SmartContractAddress = "0x15f413094Aed4D7f8f7795fC4955BE010e04c414";
const SmartContractABI = contract_abi;
const address = serverPublicKey;
const privatekey = serverPrivateKey;
const rpcurl = "https://rinkeby.infura.io/v3/e64a8dd6019a46a2b9c26f3fd4eee57b";

// Server Password
const serverPassword = process.env.serverPassword;
const serverPasswordBytes = aesjs.utils.utf8.toBytes(serverPassword);
// Client Password
const clientPassword = process.env.clientPassword;
const clientPasswordBytes = aesjs.utils.utf8.toBytes(clientPassword);
//

app.get("/",(req,res)=>{
  res.send("SHA8AAAALLLLL")
})

app.post("/add-data", async (req, res) => {
  console.log("ADD PATIENT");
  try {
    // Initialization of contract
    const provider = new Provider(privatekey, rpcurl);
    const web3 = new Web3(provider);
    const myContract = new web3.eth.Contract(
      SmartContractABI,
      SmartContractAddress
    );
    try {
      // DECODING AND PARSING DATA
      const data = convertFromJsonToArray(req.body.data);
      const decryptedData = decryptAndCheckSignAndCheckPassword(data);
      //console.log(decryptedData)
      const encryptedData = await encryptJSON(decryptedData);
      try {
        //storeVisitRecord patientId,doctorId,encrypted data, general or not                           from serverAddress
        const contract = await myContract.methods
          .storeVisitRecord(
            decryptedData.patient_key,
            decryptedData.doctor_key,
            encryptedData,
            decryptedData.general
          )
          .send({ from: address });
        res.send("SUCCESS");
      } catch (error) {
        console.log(error);
        if (
          error.message.includes(
            "The Patient already has a General Info Record"
          )
        )
          return res.status(505).send("Patient already has general record");
        return res.status(400).send("ERROR INSERTING RECORD");
      }
    } catch (error) {
      console.log(error);
      res.status(408).send("WRONG PASSWORD");
    }
  } catch (error) {
    console.log(error);
    res.status(500).send("ERROR IN INITIALIZATION");
  }
});

app.post("/get-patient", async (req, res) => {
  console.log("GET PATIENT");
  try {
    // Initialization of contract
    const provider = new Provider(privatekey, rpcurl);
    const web3 = new Web3(provider);
    const myContract = new web3.eth.Contract(
      SmartContractABI,
      SmartContractAddress
    );
    try {
      // GET PLAIN TEXT DATA
      const data = req.body.data;

      try {
        // Retreieve record from contract
        const records=await myContract.methods.retrieve(data.patient_key).call({ from: address })
        const decryptedRecords=[]
        for(let i=0;i<records.length;i++){
            const record=records[i]
            // THis is only decryption
            const decryptedMessage=await decryptAndEncryptMessage(record.message)
            const str=aesjs.utils.utf8.fromBytes(decryptedMessage)
            const json=JSON.parse(str)
            const body={
              date:json.date,
              data:json.message
            }
            decryptedRecords.push(body)
        }
        // Encrypt for client

        const encryptedBody=await encryptForClient(decryptedRecords)

        return res.send({body:encryptedBody})

      } catch (error) {

        console.log(error);

        if(error.message.includes("This Patient doesn't have any records.")) return res.status(444).send()
        if (
          error.message.includes(
            "The Patient already has a General Info Record"
          )
        )
          return res.status(505).send("Patient already has general record");
        return res.status(400).send("ERROR INSERTING RECORD");
      }
    } catch (error) {
      console.log(error);
      res.status(408).send("WRONG PASSWORD");
    }
  } catch (error) {
    console.log(error);
    res.status(500).send("ERROR IN INITIALIZATION");
  }
});


app.listen(process.env.PORT || port,()=>{
  console.log("listening on", port);
});


const convertFromJsonToArray = (json) => {
  return Object.values(json);
};


const encryptForClient=async(decryptedRecords)=>{
  const body={
    records:decryptedRecords
  }
  const body_string=JSON.stringify(body)
  const decryptedBytes=aesjs.utils.utf8.toBytes(body_string)
    // Initalize Client Crypter
    const clientCrypter = await new aesjs.ModeOfOperation.ctr(
      clientPasswordBytes,
      new aesjs.Counter(5)
    );

    const encryptedBytes=clientCrypter.encrypt(decryptedBytes)
    return encryptedBytes

}

const encryptJSON = async (JSONmessage) => {
  //Initialize Server Crypter
  const serverCrypter = new aesjs.ModeOfOperation.ctr(
    serverPasswordBytes,
    new aesjs.Counter(5)
  );
  // Encrypt Server Message
  const message_string = JSON.stringify(JSONmessage);
  const message_String_bytes = aesjs.utils.utf8.toBytes(message_string);
  const encryptedBytes = serverCrypter.encrypt(message_String_bytes);
  return Array.from(encryptedBytes);
};

const decryptAndEncryptMessage = async (encryptedServerMessage) => {
  encryptedServerMessage=Uint8Array.from(encryptedServerMessage)
  // Initialize Server Crypter
  const serverCrypter = await new aesjs.ModeOfOperation.ctr(
    serverPasswordBytes,
    new aesjs.Counter(5)
  );

  // Decrypt Server Message
  const decryptedServerMessage=await serverCrypter.decrypt(encryptedServerMessage)
  return decryptedServerMessage;
};

const decryptAndCheckSignAndCheckPassword = (message) => {
  var clientCrypter = new aesjs.ModeOfOperation.ctr(
    clientPasswordBytes,
    new aesjs.Counter(5)
  );
  const decryptedBytes = clientCrypter.decrypt(message);
  const decryptedText = aesjs.utils.utf8.fromBytes(decryptedBytes);
  const json = JSON.parse(decryptedText);
  if (json.password != clientPassword) throw new Error("WRONG PASSWORD");
  const address = sigUtil.recoverPersonalSignature({
    data: json.message_hash,
    signature: json.message_sign,
  });
  if (json.doctor_key !== address) throw new Error("UNVERIFIED USER");
  return json;
};

//Error: PollingBlockTracker - encountered an error while attempting to update latest block:
