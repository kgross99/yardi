Account subclass: SavingsAccount


    interest:=0;

    initSavingsAccount:actnumber customername actbalance) [
	number:=actnumber;
	customer:=customername;
	balance=actbalance;
    ]

    accrue: rate [
	balance:=balance +balance*rate;
	interest:=interest +balance*rate;
    ]

]
