export const encrypt=(password,message)=>{
    var passwordBytes = window.aesjs.utils.utf8.toBytes(password);
    var messageBytes = window.aesjs.utils.utf8.toBytes(message);
    var aesCtr = new window.aesjs.ModeOfOperation.ctr(passwordBytes, new window.aesjs.Counter(5));
    var encryptedBytes = aesCtr.encrypt(messageBytes);
    return encryptedBytes;
}

export const decrypt=(password,message)=>{
    console.log(message)
    const encryptedBytes=convertFromJsonToArray(message)
    var passwordBytes = window.aesjs.utils.utf8.toBytes(password);
    var aesCtr = new window.aesjs.ModeOfOperation.ctr(passwordBytes, new window.aesjs.Counter(5));
    const decryptedBytes=aesCtr.decrypt(encryptedBytes)
    const message_string=window.aesjs.utils.utf8.fromBytes(decryptedBytes)
    const json=JSON.parse(message_string)
    return json.records
}

const convertFromJsonToArray = (json) => {
    return Object.values(json);
  };
  
