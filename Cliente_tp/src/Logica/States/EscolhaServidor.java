/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica.States;

import Logica.ClienteDados;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class EscolhaServidor extends StateAdapter
{ 
  
    public EscolhaServidor(ClienteDados cd) 
    {
        super(cd);
    }

    @Override
    public IStates escolha_do_servidor(int pos) {     
        try {
            
            if(getCliente().estabelece_ligacao_servidor(pos)) //caso efectue a ligação com sucesso
                return new Login_Registar(getCliente()); //muda para o estado seguinte
            
            return this; //senao fica no mesmo
       
        } catch (IOException ex) {
            return this;
        }
    }
    
    
    
}
