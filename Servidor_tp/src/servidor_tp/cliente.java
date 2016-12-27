/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor_tp;

import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
class Pasta {

    ArrayList<Pasta> pastas;
    ArrayList<String> ficheiroTxt;
    ArrayList<String> ficheiroJpg;

    Pasta() {
        pastas = new ArrayList<Pasta> ();
        ficheiroTxt = new ArrayList<String>();
        ficheiroJpg = new ArrayList<String>();
    }

    void addPasta(Pasta p) {
        pastas.add(p);
    }

    void addFicheiroTxt(String t) {
        ficheiroTxt.add(t);
    }

    void addFciheiroJpg(String j) {
        ficheiroJpg.add(j);
    }

    ArrayList<Pasta> getPastas() {
        return pastas;
    }

    ArrayList<String> getFiceirosTxt() {
        return ficheiroTxt;
    }

    ArrayList<String> getFicheirosJpg() {
        return ficheiroJpg;
    }
}

class AmbienteTrabalho extends Pasta {
    Pasta pastaAtual;
    private String caminho;

    public AmbienteTrabalho(String log_nome) {
        pastaAtual=this;
        caminho = "C:/Users/" + log_nome + "/Desktop";
    }
    
    public void setPastaAtual(Pasta p){pastaAtual=p;}
    
    Pasta listarConteudoPastaAtual(){
        return pastaAtual;
    }
    
    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String c) {
        caminho = c;
    }
    
    
}

public class cliente {

    private String log_nome;
    private String password;
    AmbienteTrabalho desktop;

    public cliente(String log_nome, String password) {
        this.log_nome = log_nome;
        this.password = password;
        desktop = new AmbienteTrabalho(log_nome);
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
