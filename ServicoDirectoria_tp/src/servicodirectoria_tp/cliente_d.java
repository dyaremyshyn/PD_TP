/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicodirectoria_tp;

/**
 *
 * @author Sergio
 */
public class cliente_d {
    String IP;
    String port;
    String nome_servidor;
    String nome_cliente;

    public cliente_d(String IP, String port, String nome_servidor, String nome_cliente) {
        this.IP = IP;
        this.port = port;
        this.nome_servidor = nome_servidor;
        this.nome_cliente = nome_cliente;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNome_servidor() {
        return nome_servidor;
    }

    public void setNome_servidor(String nome_servidor) {
        this.nome_servidor = nome_servidor;
    }

    public String getNome_cliente() {
        return nome_cliente;
    }

    public void setNome_cliente(String nome_cliente) {
        this.nome_cliente = nome_cliente;
    }
    
    
    
    
}
