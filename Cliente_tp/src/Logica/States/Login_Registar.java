/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica.States;

import Logica.ClienteDados;

/**
 *
 * @author Sergio
 */
public class Login_Registar extends StateAdapter
{ 
  
    public Login_Registar(ClienteDados cd) 
    {
        super(cd);
    }

    @Override
    public IStates Register(String nome, String pass) {
      
      getCliente().registar(nome, pass);
        
      return this;
    }

    @Override
    public IStates Login(String nome, String pass) {
      
        if(getCliente().login(nome, pass))
        return new Comandos(getCliente());
        
        return this;
    }
    
    
    
}
