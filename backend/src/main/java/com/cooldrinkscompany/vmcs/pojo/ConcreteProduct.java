package com.cooldrinkscompany.vmcs.pojo;
{  
    class VendingDrink : Drink  
    {  
        private int _id;
        private String _name;
        private int _quantity;
        private double _price = 0.0d;
  
        public VendingDrink(int id, string name)  
        {  
            _id = id;
            _name = name;
        }
    }  
}  