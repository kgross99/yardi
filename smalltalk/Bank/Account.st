
"account.st" 
"A program which creates a account class" 

Object subclass: account [ 
|number customer balance|

getbalance [ 
		"An instance method to get the accounts balance." 
		<category: 'accessing'> 

		^balance
	] 
setbalance: newBalance [

		"An instance method to set the accounts balance." 
		<category: 'accessing'> 
		balance:= newBalance
	]

 deposit: amount[
	balance := balance + amount;
    ]

   withdraw: amount [
	balance=balance -amount;
    ]




accrue: rate [
^self subclassResponsibility
]
