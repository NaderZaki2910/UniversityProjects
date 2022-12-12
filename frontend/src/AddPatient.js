import React, { useEffect } from "react";
import { Formik, Form, Field } from "formik";
import { sha256 } from "js-sha256";
import { Moralis } from "moralis";
import { encrypt } from "./METHODS";
import axios from "axios";

const serverUrl = "https://fte4ajr1ecuv.usemoralis.com:2053/server";
const appId = "gu2NSIijo65u7hVO1otneuNoPlw29tMQg16O3D26";
//const url = "https://hospital-3a2leen.herokuapp.com";
const url="https://hospital-maganeen.herokuapp.com"
function AddPatient() {
  useEffect(() => {
    Moralis.start({ serverUrl, appId });
  }, []);

  const submit = async (values) => {
    console.log(values);
    const message = {
      name: values.name,
      age: values.age,
      height: values.height,
      weight: values.weight,
      gender: values.gender,
    };
    const message_string = JSON.stringify(message);
    const message_hash = "0x" + sha256(message_string);
    const message_sign = await window.ethereum.request({
      method: "personal_sign",
      params: [Moralis.User.current().get("ethAddress"), message_hash],
    });
    const body = {
      general: true,
      date: new Date(),
      message: message_string,
      message_sign: message_sign,
      message_hash: message_hash,
      password: values.password,
      patient_key: values.patient_key,
      doctor_key: Moralis.User.current().get("ethAddress"),
    };
    const body_string = JSON.stringify(body);
    const encryptedBody = encrypt(values.password, body_string);
    axios
      .post(url + "/add-data", { data: encryptedBody })
      .then((res) => {
        console.log(res);
        alert("Patient added successfully")
      })
      .catch((error) => {
          
        console.log(error.response);
        const code = error.response.status;
        if (code === 408) return alert("wrong password");
        if (code === 505) return alert("Patient already has a general record");
        return alert("an error occurred")

      });
  };
  return (
    <div className="container text-center w-25">
      <h1>Add New Patient</h1>
      <Formik
        initialValues={{
          name: "",
          age: "",
          height: "",
          weight: "",
          gender: "",
          patient_key: "",
          password: "",
        }}
        onSubmit={submit}
      >
        {() => {
          return (
            <Form>
              <div className="text-center mb-3">
                <label className="form-label">Name</label>
                <Field name="name" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Age</label>
                <Field name="age" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Height</label>
                <Field name="height" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Weight</label>
                <Field name="weight" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Gender</label>
                <Field name="gender" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Patient Public key</label>
                <Field name="patient_key" className="form-control" />
              </div>

              <div className="mb-3">
                <label className="form-label">Password</label>
                <Field name="password" className="form-control" />
              </div>

              <button type="submit" className="btn btn-primary">
                submit
              </button>
            </Form>
          );
        }}
      </Formik>
    </div>
  );
}

export default AddPatient;
