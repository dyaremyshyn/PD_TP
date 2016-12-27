/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor_tp;

/**
 *
 * @author Sergio
 */
public class cliente {
    
    private String log_nome;
    private String  password;
    
    public cliente(String log_nome, String password){
    this.log_nome = log_nome;
    this.password = password;
    }

    public String getLog_nome() {
        return log_nome;
    }

    public void setLog_nome(String log_nome) {
        this.log_nome = log_nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    } 
}
