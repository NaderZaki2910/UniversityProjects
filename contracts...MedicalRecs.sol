// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

/**
 * @title Storage
 * @dev Store & retrieve value in a variable
 * @custom:dev-run-script ./scripts/deploy_with_ethers.ts
 */
contract ElectronicMedicalRecords {

    event RecordsAdded(address hospital, address patient, address doctor, string message);

    uint private countRecords = 0;

    struct Hospital {
        address HospitalAddress;
        mapping (address => uint) patientRecords;
    }

    struct Record {
        string message;
        address doctor;
        uint date;
        uint transactionId;
        uint previousTransactionId;
        bool general;
    }

    mapping (address => Hospital) private hospitals;
    mapping (uint => Record) private records;


    function storeVisitRecord(address patient, address doctorIn, string memory messageIn, bool generalRecord) public {
        if(hospitals[msg.sender].HospitalAddress != address(0)){
            bool iterate = true;
            uint lastRecord = hospitals[msg.sender].patientRecords[patient];
            while(iterate){
                if(lastRecord == 0){
                    bool condCheck = (generalRecord != true);
                    require(condCheck,"This Transaction is the first record and it's not the General Info of the patient");
                    hospitals[msg.sender].patientRecords[patient] = ++countRecords;
                    records[hospitals[msg.sender].patientRecords[patient]] = Record(messageIn,doctorIn,block.timestamp,hospitals[msg.sender].patientRecords[patient],hospitals[msg.sender].patientRecords[patient],generalRecord);
                    iterate = false;
                    break;
                }
                else{
                    require(!generalRecord,"The Patient already has a General Info Record");
                    Record memory recordValue = records[lastRecord];
                    bool stringComp = keccak256(abi.encodePacked(recordValue.message)) != keccak256(abi.encodePacked(messageIn));
                    bool condCheck = ( stringComp || (recordValue.doctor != doctorIn));
                    require(condCheck,"This Transaction is repeated (same data, same doctor, same patient)");
                    if(recordValue.general == true){
                        uint RecId = ++countRecords;
                        records[RecId] = Record(messageIn,doctorIn,block.timestamp,RecId,hospitals[msg.sender].patientRecords[patient],generalRecord);
                        hospitals[msg.sender].patientRecords[patient] = RecId;
                        iterate = false;
                        break;
                    }
                    else{
                        lastRecord = recordValue.previousTransactionId;
                    }
                }
            }
            emit RecordsAdded(msg.sender, patient, doctorIn, messageIn);
        }
        else{
            bool condCheck = (generalRecord);
            require(condCheck,"This Transaction is the first record and it's not the General Info of the patient");
            hospitals[msg.sender].HospitalAddress = msg.sender;
            hospitals[msg.sender].patientRecords[patient] = ++countRecords;
            records[hospitals[msg.sender].patientRecords[patient]] = Record(messageIn,doctorIn,block.timestamp,hospitals[msg.sender].patientRecords[patient],hospitals[msg.sender].patientRecords[patient],true);
            emit RecordsAdded(msg.sender, patient, doctorIn, messageIn);
        }
    }

    function retrieve(address patient) public view returns (Record[] memory){
        uint lastRecord = hospitals[msg.sender].patientRecords[patient]; 
        bool iterate = true;
        uint count = 0;
        require(lastRecord != 0,"This Patient doesn't have any records.");
        while(iterate){
            Record memory r = records[lastRecord];
            count++;
            lastRecord = r.previousTransactionId;
            if(r.general){
                iterate = false;
                break;
            }
        }
        Record[] memory recordsRetrieved = new Record[](count);
        lastRecord = hospitals[msg.sender].patientRecords[patient]; 
        iterate = true;
        while(iterate){
            Record memory r = records[lastRecord];
            count--;
            recordsRetrieved[count] = r;
            lastRecord = r.previousTransactionId;
            if(r.previousTransactionId == r.transactionId){
                iterate = false;
                break;
            }
        }
        return recordsRetrieved;
    }
}