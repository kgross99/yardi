Object subclass: Customer [ 
	|custName| 
	getname[ 
		"An instance method to get the accounts balance." 
		<category: 'accessing'> 

		^custName
	] 
 

 "An initializer (e.g., init) must be an instance method."
    init [
        custName:='Bob'.
    ]
	setName: name [ 
		<category: 'accessing'> 
		custName := name. 
	
] 
	Customer class [
        new [
            ^super new init
        ]
]
]
