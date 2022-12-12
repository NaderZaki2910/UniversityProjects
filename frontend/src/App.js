import Dashboard from './Dashboard';
import { Route } from 'react-router-dom';
import Login from './Login';
import {BrowserRouter, Routes} from "react-router-dom"
import { useEffect } from 'react';
import {Moralis} from 'moralis';
import AddPatient from './AddPatient';
import Patient from './Patient';





function App() {
  return (
    <BrowserRouter>
     <Routes>
     <Route path="/" element={<Login />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/add-patient" element={<AddPatient />} />
      <Route path="/patient" element={<Patient />} />
    </Routes>
</BrowserRouter>
  );
}

export default App;
