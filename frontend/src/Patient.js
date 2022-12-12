import React, { useState,useEffect } from "react";
import axios from "axios";
import { Moralis } from "moralis";
import { sha256 } from "js-sha256";
import { encrypt,decrypt } from "./METHODS";
import { Navigate, useNavigate } from "react-router-dom";


const serverUrl = "https://fte4ajr1ecuv.usemoralis.com:2053/server";
const appId = "gu2NSIijo65u7hVO1otneuNoPlw29tMQg16O3D26";
//const url = "https://hospital-3a2leen.herokuapp.com";
const url="https://hospital-maganeen.herokuapp.com"

function Patient() {
    const navigate=useNavigate()
    useEffect(() => {
        Moralis.start({ serverUrl, appId });
      }, []);

  const [patientKey, setPatientKey] = useState("");
  const [password,setPassword]=useState("");
  const [patientData,setPatientData]=useState("")
  const [prescription,setPrescription]=useState("")
  const [bloodPressure,setBloodPressure]=useState("")
  const [visitReason,setVisitReason]=useState("")
  const [misc,setMisc]=useState("")
  const [diagnosis,setDiagnosis]=useState("")


  const submit = async () => {
    const message = {
      bloodPressure: bloodPressure,
      misc: misc,
      prescription:prescription,
      visitReason: visitReason,
      diagnosis: diagnosis,
    };
    const message_string = JSON.stringify(message);
    const message_hash = "0x" + sha256(message_string);
    const message_sign = await window.ethereum.request({
      method: "personal_sign",
      params: [Moralis.User.current().get("ethAddress"), message_hash],
    });
    const body = {
      general: false,
      date: new Date(),
      message: message_string,
      message_sign: message_sign,
      message_hash: message_hash,
      password: password,
      patient_key: patientKey,
      doctor_key: Moralis.User.current().get("ethAddress"),
    };
    const body_string = JSON.stringify(body);
    const encryptedBody = encrypt(password, body_string);
    axios
      .post(url + "/add-data", { data: encryptedBody })
      .then((res) => {
          alert("Record Added Successfully")
        console.log(res);
      })
      .catch((error) => {
          
        console.log(error.response);
        const code = error.response.status;
        if (code === 408) return alert("wrong password");
        if (code === 505) return alert("Patient already has a general record");
        return alert("an error occurred")

      });
  };
  const getPatientData = () => {
      if(!patientKey) return alert("PATIENT KEY NEEDED")
      if(!password) return alert("PASSWORD NEDDED")
      const body = {
        // date does not matter
        date: new Date(),
        patient_key: patientKey,
        // doctor key does not matter
        doctor_key: Moralis.User.current().get("ethAddress"),
      };
      //const body_string = JSON.stringify(body);
      //const encryptedBody = encrypt(password, body_string);
      axios
        .post(url + "/get-patient", { data: body })
        .then(async(res) => {
            console.log(res)
          const records=await decrypt(password,res.data.body)
          console.log(records)
          const parsedRecords=[]
          for(let i=0;i<records.length;i++){
              parsedRecords.push({
                  date:new Date(records[i].date),
                  data:JSON.parse(records[i].data)
              })
          }
          
          setPatientData(parsedRecords)
          setPassword("")
          console.log(parsedRecords)
        })
        .catch((error) => {
          console.log(error);
          const code = error.response.status;
          if(code ===444){
              alert("This Patient doesn't have any records.");
              navigate("/add-patient")
              return;
          }
          if (code === 408) return alert("wrong password");
          if (code === 505) return alert("Patient already has a general record");
          return alert("an error occurred")
  
        });
  };

  return (
    <div className="container text-center w-25">
        <h1 className="mb-10">Patient Records</h1>
      {!patientData ? (
        <div>
          <h3>Enter Patient Key</h3>
          <div className="text-center mb-3">
            <label className="form-label">Patient Key</label>
            <input
              name="patient-key"
              className="form-control"
              value={patientKey}
              onChange={(value) => setPatientKey(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Password</label>
            <input
              name="password"
              className="form-control"
              value={password}
              onChange={(value) => setPassword(value.target.value)}
            />
          </div>
          <div>
              <button className="btn btn-primary" type="button" onClick={getPatientData}>Retrieve patient records</button>
              </div>
        </div>
      ) : null}


      {patientData?(
      <div>
            <div>
                <h3> GENERAL RECORD</h3>
              <p>Name: {patientData[0].data.name}</p>
              <p>Age: {patientData[0].data.age}</p>
              <p>Height: {patientData[0].data.height}</p>
              <p>Weight: {patientData[0].data.weight}</p>
              <p>Gender: {patientData[0].data.gender}</p>
          </div>
          <h3> PREVIOUS VISITS </h3>
          {patientData.length<2?<h4>No records available</h4>:null}
          {patientData.map((rec,i)=>{
              if(i==0) return null
              return(
                  <div>
                      <h4>DATE: {rec.date.toString()}</h4>
                <p>Blood pressure: {rec.data.bloodPressure}</p>
                <p>Misc: {rec.data.misc}</p>
                <p> visit reason: {rec.data.visitReason}</p>
                <p> Prescription: {rec.data.prescription}</p>
                <p> Diagnosis: {rec.data.diagnosis}</p>
                </div>
              )
          })}
         
         
          <div>
        <h3> Add new record</h3>
        <div className="text-center mb-3">
            <label className="form-label">blood pressure</label>
            <input
              name="blood-pressure"
              className="form-control"
              value={bloodPressure}
              onChange={(value) => setBloodPressure(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Misc.</label>
            <input
              name="Misc"
              className="form-control"
              value={misc}
              onChange={(value) =>setMisc(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Visis reason</label>
            <input
              name="visit-reason"
              className="form-control"
              value={visitReason}
              onChange={(value) => setVisitReason(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Prescription</label>
            <input
              name="patient-key"
              className="form-control"
              value={prescription}
              onChange={(value) => setPrescription(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Diagnosis</label>
            <input
              name="patient-key"
              className="form-control"
              value={diagnosis}
              onChange={(value) => setDiagnosis(value.target.value)}
            />
          </div>
          <div className="text-center mb-3">
            <label className="form-label">Password</label>
            <input
              name="password"
              className="form-control"
              value={password}
              onChange={(value) => setPassword(value.target.value)}
            />
          </div>
          </div>
          <button type="button" onClick={submit}> Add new record </button>

      </div>
      
      ):null}
    </div>
  );
}

export default Patient;
