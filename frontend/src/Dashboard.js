import React, { useEffect, useState } from "react";
import { Moralis } from "moralis";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { sha256 } from 'js-sha256';
import { encryptPublicLong, decryptPrivateLong } from '@lsqswl/rsaencrypt'

const serverUrl = "https://fte4ajr1ecuv.usemoralis.com:2053/server";
const appId = "gu2NSIijo65u7hVO1otneuNoPlw29tMQg16O3D26";
const serverPublicKey = "0x46379f914B6B9e648F282772e5F5c44D4A457F88";
const url="http://localhost:5000/"
//hash public key of user, password, data,hash of data
// sign message
// sign= encrypt with private key
function Dashboard() {
  const navigate=useNavigate()

  useEffect(() => {
    Moralis.start({ serverUrl, appId });
  }, []);
  // console.log(Moralis.User.current())
  // const [rink, updateRink] = useState("");
  // const [eth, updateEth] = useState("");


  // const ethSign = async () => {
  //   try {
  //     const message="eeeeeehhhhh"
  //     const message_hash=sha256(message)
  //     const message_sign = await window.ethereum.request({
  //       method: "personal_sign",
  //       params: [Moralis.User.current().get("ethAddress"), message_hash],
  //     });
  
  //     // create json and stringify

  //     const json={
  //       "message":message,
  //       "message_hash":message_hash,
  //       "message_sign":message_sign,
  //       "password":"mesh 3aref",
  //       "password_hash":sha256("mesh 3aref"),
  //       "public_key":Moralis.User.current().get("ethAddress")

  //     }
  //     const json_string=JSON.stringify(json);


  //   } catch (err) {
  //     console.error(err);
  //   }
    
  // };

  const route=(to)=>{
    navigate(`/${to}`)
  }

  return (
    <div className="container text-center ">
      <h1 className="mt=10">ta3ala ekshef 3alaya hhhhhh</h1>
      <h4 className="btn btn-secondary" onClick={()=>route("add-patient")}> Add new patient</h4>
      <br/>
      <h4 className="btn btn-secondary" onClick={()=>route("patient")}> View patient</h4>
    </div>
  );
}

export default Dashboard;
