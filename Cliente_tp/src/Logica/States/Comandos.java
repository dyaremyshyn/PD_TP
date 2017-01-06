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
public class Comandos extends StateAdapter{
    
    public Comandos(ClienteDados cliente_dados) {
        super(cliente_dados);
    }

    @Override
    public IStates Logout() {
        
        if(getCliente().logout())
         return new EscolhaServidor(getCliente()); //muda para o estado login_registar
        
        return this;
    }
    
    
    
    
}
