/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente_tp;

import Logica.Cliente;
import UI_text.TextuserInterface;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Sergio
 */
public class Cliente_tp {

    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) throws IOException, ClassNotFoundException 
    {
        int dirPort ;
       InetAddress dirAddr;
        
          if(args.length != 2){
            System.out.println("Sintaxe: java Client directoriaAddress serverUdpPort");
            return;
        }
             
         try {
             dirAddr = InetAddress.getByName(args[0]);
             dirPort = Integer.parseInt(args[1]);
         } catch (UnknownHostException ex) {
             return;
         }
          
        TextuserInterface iuTexto = new TextuserInterface(new Cliente(dirAddr,dirPort));
        iuTexto.corre();
    }  
    
}
