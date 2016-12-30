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

    String pastas;
    ArrayList<String> ficheiroTxt;
    ArrayList<String> ficheiroJpg;

    Pasta(String nomePasta) {
        pastas = nomePasta;
        ficheiroTxt = new ArrayList<String>();
        ficheiroJpg = new ArrayList<String>();
    }

    void setPasta(String s){pastas=s;}
    String getPasta(){return pastas;}

    boolean pastaVazia(){
        if (ficheiroJpg.isEmpty() && ficheiroTxt.isEmpty())
            return true;
        return false;
    }
    
    void addFicheiroTxt(String t) {
        ficheiroTxt.add(t);
    }

    void addFciheiroJpg(String j) {
        ficheiroJpg.add(j);
    }

    void delFTxt(String p){
        for(int i = 0;i<ficheiroTxt.size();i++)
            if(ficheiroTxt.get(i).equals(p)) 
                ficheiroTxt.remove(i);
        
    }
    
    void delFJpg(String p){
        for(int i = 0;i<ficheiroJpg.size();i++)
            if(ficheiroJpg.get(i).equals(p)) 
                ficheiroJpg.remove(i);
    }

    ArrayList<String> getFiceirosTxt() {
        return ficheiroTxt;
    }

    ArrayList<String> getFicheirosJpg() {
        return ficheiroJpg;
    }
    
    
    
}



class AmbienteTrabalho extends Pasta {
    ArrayList<Pasta> dir;
    ArrayList<String> txt;
    ArrayList<String> jpg;
    
    Pasta pastaAtual;
    private String caminho;

    public AmbienteTrabalho(String log_nome, String nomePasta) {
        super(nomePasta);
        dir = new ArrayList<Pasta>();
        txt = new ArrayList<String>();
        jpg = new ArrayList<String>();
        
        pastaAtual = this;
        caminho = "C:/Users/" + log_nome + "/" + nomePasta;
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
    
    void addDir(Pasta p) {
        dir.add(p);
    }

    ArrayList<Pasta> getDir() {
        return dir;
    }
    
    /*boolean existePasta(String p){
        for(int i=0;i<dir.size();i++) 
            if(dir.get(i).getPasta().equals(p)) 
                return true;
        
        return false;
    }
    */
    
    
    boolean delPasta(String nomePasta){
        for(int i =0 ; i< dir.size();i++)
            if(dir.get(i).getPasta().equals(nomePasta))
                if(dir.get(i).pastaVazia()){
                    dir.remove(i);
                    return true;
                }
        return false;
    }

    void apagaFicheiro(String s){
        String terminacao="";
        for(int i=0;i<s.length();i++)
            if(s.charAt(i)== '.')
                terminacao+=s.charAt(i);
        
        if(terminacao.equals(".txt"))
            delFTxt(s);
        
        else if(terminacao.equals(".jpg"))
            delFJpg(s);
        
        else if(terminacao.equals(".dir"))
            delPasta(s);
    }
}

public class cliente {

    private String log_nome;
    private String password;
    AmbienteTrabalho desktop;

    public cliente(String log_nome, String password) {
        this.log_nome = log_nome;
        this.password = password;
        desktop = new AmbienteTrabalho(log_nome, "Desktop");
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
