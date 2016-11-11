Account subclass: CheckingAccount [

    init:  [
   					number:=0.
					customer:='bob'.
					balance:=0.
   					 ]
   update: num name amount[
   number:=num.
   customer:=name.
   balance:=amount]
  accrue: rate[]
				
				
	CheckingAccount class [
        new [
            ^super new init
        ]

		]
		]
