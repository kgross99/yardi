
"bank.st" 
"A program which creates a bank class" 

Object subclass: Bank [ 
	
 accounts := OrderedCollection new.  

	addaccount: account [
		"An instance method to add the account to the bank" 
		<category: 'accessing'> 
		accounts add: account]

	accrue:rate [
		accounts do: [account accrue: rate]
 
	banklist := bank new
	cust := customer new
	cust setname: 'Ann'
       checking := CheckingAccount new
	checking initCheckingAccount: '01001' cust 100.00
	banklist addaccount: checking
	savings := SavingsAccount new
	savings initSavingsAccount:  '01002 cust 200.00

	banklist addaccount: savings

	banklist accrue: 0.02
	
	
	
	

]
]
