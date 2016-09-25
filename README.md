# N26 programming challenge [![Build Status](https://travis-ci.org/KernelPanicAUS/n26.svg?branch=master)](https://travis-ci.org/KernelPanicAUS/n26)

## Curls

### Create Transaction
```
curl -H "Accept: application/json" -H "Content-type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/455 -d '{"amount": 5002, "type": "cars"}'
```

### Create Child Transaction
```
curl -H "Accept: application/json" -H "Content-type: application/json" -X PUT http://localhost:8080/transactionservice/transaction/456 -d '{"amount": 10000, "type": "cars", "parent_id": 455}'
```

### Get Transaction ids for type
```
curl http://localhost:8080/transactionservice/types/car
```

### Get Transaction sum
```
curl http://localhost:8080/transactionservice/sum/455
```
