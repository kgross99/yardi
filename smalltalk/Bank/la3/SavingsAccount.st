Account subclass: SavingsAccount[
       |interest|
    
    initSavingsAccount: actnumber customername actbalance [
    interest:=0.
	number:=actnumber;
	customer:=customername;
	balance=actbalance;
    ]

    accrue: rate [
	balance:=balance +balance*rate;
	interest:=interest +balance*rate;
    ]

]
