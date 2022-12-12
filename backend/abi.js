module.exports= abi=[
	{
		"anonymous": false,
		"inputs": [
			{
				"indexed": false,
				"internalType": "address",
				"name": "hospital",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "address",
				"name": "patient",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "address",
				"name": "doctor",
				"type": "address"
			},
			{
				"indexed": false,
				"internalType": "uint256[]",
				"name": "message",
				"type": "uint256[]"
			}
		],
		"name": "RecordsAdded",
		"type": "event"
	},
	{
		"inputs": [
			{
				"internalType": "address",
				"name": "patient",
				"type": "address"
			},
			{
				"internalType": "address",
				"name": "doctorIn",
				"type": "address"
			},
			{
				"internalType": "uint256[]",
				"name": "messageIn",
				"type": "uint256[]"
			},
			{
				"internalType": "bool",
				"name": "generalRecord",
				"type": "bool"
			}
		],
		"name": "storeVisitRecord",
		"outputs": [],
		"stateMutability": "nonpayable",
		"type": "function"
	},
	{
		"inputs": [
			{
				"internalType": "address",
				"name": "patient",
				"type": "address"
			}
		],
		"name": "retrieve",
		"outputs": [
			{
				"components": [
					{
						"internalType": "uint256[]",
						"name": "message",
						"type": "uint256[]"
					},
					{
						"internalType": "address",
						"name": "doctor",
						"type": "address"
					},
					{
						"internalType": "uint256",
						"name": "date",
						"type": "uint256"
					},
					{
						"internalType": "uint256",
						"name": "transactionId",
						"type": "uint256"
					},
					{
						"internalType": "uint256",
						"name": "previousTransactionId",
						"type": "uint256"
					},
					{
						"internalType": "bool",
						"name": "general",
						"type": "bool"
					}
				],
				"internalType": "struct ElectronicMedicalRecords.Record[]",
				"name": "",
				"type": "tuple[]"
			}
		],
		"stateMutability": "view",
		"type": "function"
	}
]