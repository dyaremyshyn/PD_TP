/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor_tp;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Sergio & Dmytro
 */


class Pasta {

    String nomeFicheiro;
    ArrayList<File> ficheiros; //tanto podem ser pastas como outro tipo de ficheiros 

    Pasta(String nomeFicheiro) {
        this.nomeFicheiro = nomeFicheiro;
        ficheiros = new ArrayList<File>();
    }

    void setPasta(String s){nomeFicheiro=s;}
    String getPasta(){return nomeFicheiro;}

    boolean pastaVazia(){
        if (ficheiros.isEmpty())
            return true;
        return false;
    }
    
    void addFicheiro(File f) {
        ficheiros.add(f);
    }


    void delFich(String p){
        for(int i = 0;i<ficheiros.size();i++)
            if(!ficheiros.get(i).isDirectory()){ 
                
                String[] par = ficheiros.get(i).getAbsolutePath().split("/");
                if(par[par.length-1].equals(p))
                    ficheiros.remove(i);
                
            }
    }
    
    void delDir(String p){
        for(int i = 0;i<ficheiros.size();i++)
            if(ficheiros.get(i).isDirectory()){ 
                
                String[] par = ficheiros.get(i).getAbsolutePath().split("/");
                if(par[par.length-1].equals(p))
                    ficheiros.remove(i);
                
            }
    }

    ArrayList<File> getFiceiros() {
        return ficheiros;
    }
    
}



class AmbienteTrabalho extends Pasta {
    ArrayList<Pasta> dir;
    Pasta pastaAtual;
    private String caminho;

    public AmbienteTrabalho(String log_nome, String nomeFicheiro) {
        super(nomeFicheiro);
        dir = new ArrayList<Pasta>();
        
        pastaAtual = this;
        caminho = "C:/Users/" + log_nome + "/" + nomeFicheiro;
    }
    
    public void setPastaAtual(Pasta p){
        pastaAtual=p;
    }
    
    String listarConteudoPastaAtual(){
        String c="";
        for(int i=0;i<dir.size();i++){
            c = dir.get(i).getPasta()+"\n";
        }
        return c;
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

}

public class cliente {

    private String log_nome;
    private String password;
    AmbienteTrabalho desktop;
    boolean loginEfetuado;

    public cliente(String log_nome, String password) {
        this.log_nome = log_nome;
        this.password = password;
        desktop = new AmbienteTrabalho(log_nome, "Desktop");
        loginEfetuado=false;
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
    public void setLoginEfetuado(boolean b) {
        loginEfetuado = b;
    }
    public boolean getLoginEfetuado(){return loginEfetuado;}
    
    public AmbienteTrabalho getAmbienteTrabalho(){return desktop;}
    
    public boolean mudarPastaTrabalho(String nome){
        for(int i=0;i<desktop.getDir().size();i++){
            if(desktop.getDir().get(i).getPasta().equals(nome)){
                desktop.setCaminho(desktop.getCaminho()+"/"+nome);
                desktop.setPastaAtual(desktop.getDir().get(i));
                return true;
            }
        }
        return false;
    }
    
    
}
