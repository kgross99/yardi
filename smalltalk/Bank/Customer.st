
Object subclass: Customer [ 
	|custName| 


	Customer class [
        new [
            ^super new init
        ]
    ]

 "An initializer (e.g., init) must be an instance method."
    init [
        custName:='Bob'.
    ]
	setName: name [ 
		
		<category: 'accessing'> 

		custName := name. 

	] 

	getname: [ 
		
		"A class method to get the customer name." 
		<category: 'accessing'> 
                 |name|
		name=: custName.
		^ name
	] 
] 

