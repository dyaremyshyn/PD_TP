/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicodirectoria_tp;

import java.io.Serializable;

/**
 *
 * @author Sergio
 */
public class servidor implements Serializable {
    
    String nome;
    String IP;
    int Port;

    public servidor(String nome, String IP, int Port) {
        this.nome = nome;
        this.IP = IP;
        this.Port = Port;
    }
    
    //get's set's

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int Port) {
        this.Port = Port;
    }
    
    @Override
    public String toString(){
        String info = "";
    info += nome + " " + IP + " " +  Port;
    return info;
    }
}
