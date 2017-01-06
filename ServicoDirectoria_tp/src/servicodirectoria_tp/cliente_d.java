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
    
    private String IP;
    private int port;
    private String nome_servidor;
    private String nome_cliente;
    long lastHeartbeat;

    public cliente_d( String nome_cliente, int port, String nome_servidor ) {
     //   this.IP = IP;
        this.port = port;
        this.nome_servidor = nome_servidor;
        this.nome_cliente = nome_cliente;
        
       updateHeartbeat();
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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
    
    //heartbeat
    public void updateHeartbeat() {
		lastHeartbeat=System.currentTimeMillis();
	}
	
	public long getLastHeartbeat() {
		return lastHeartbeat;
	}
        
           
    @Override
    public String toString(){
        String info = "";
    info += nome_cliente + " " +  port + " "+ nome_servidor;
    return info;
    }
    
}
