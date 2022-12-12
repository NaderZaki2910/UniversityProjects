import React, { useEffect } from "react";
import Sickdog from "./sickdog.png";
import { Moralis } from "moralis";
import { useNavigate } from "react-router-dom";

const serverUrl = "https://fte4ajr1ecuv.usemoralis.com:2053/server";
const appId = "gu2NSIijo65u7hVO1otneuNoPlw29tMQg16O3D26";

function Login() {
  const navigate = useNavigate();
  useEffect(() => {
    Moralis.start({ serverUrl, appId });
  }, []);

  const login = async () => {
    await Moralis.authenticate().then(() => {
      navigate("/dashboard");
    });
  };
  return (
    <div className="text-center pt-10">
      <div className="mt-20">
      <img className="mb-4" src={Sickdog} alt="" width="152" height="137" />
        <h2>Sick Pupper Inc.</h2>
      </div>

      <div className="mt-5">
      <button
            onClick={login}
            id="btn-login"
            className="btn btn-primary w-25"
            type="submit"
          >
            Sign in
          </button>
      </div>
    </div>
  );
}

export default Login;
