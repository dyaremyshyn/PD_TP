/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import Logica.States.EscolhaServidor;
import Logica.States.IStates;
import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author Sergio
 */
public class Cliente {
    
    private ClienteDados dados = new ClienteDados();
    private IStates estado;

    public Cliente(InetAddress dirAddr, int dirPort) {
        
        //regista od dados relativos ao ip e port do serviço de directoria
        dados.setSerDirectoria_Addr(dirAddr);
        dados.setSerDirectoria_Port(dirPort);       
              
        setEstado(new EscolhaServidor(dados));        
    }

    public ClienteDados getDados() {
        return dados;
    }

    public void setDados(ClienteDados dados) {
        this.dados = dados;
    }

    public IStates getEstado() {
        return estado;
    }

    public void setEstado(IStates estado) {
        this.estado = estado;
    }

    
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // funçoes get dados para text
    
    public int getSerDirectoria_Port() {
        return dados.getSerDirectoria_Port();
    }
    
    public InetAddress getSerDirectoria_Addr() {
        return dados.getSerDirectoria_Addr();
    }

    public String pedido_lista_servidores() throws IOException, ClassNotFoundException {
        return dados.pedido_lista_servidores();
    }

   
    
    
    //funções de verificação
    
    
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // funcoes delegadas no estado corrente
    public void Register(String nome, String pass) {
        setEstado( estado.Register(nome, pass));
    }

    public void Login(String nome, String pass) {
        setEstado( estado.Login(nome, pass));
    }

    public void Logout() {
        setEstado( estado.Logout());
    }

    public void CopyFile() {
        setEstado( estado.CopyFile());
    }

    public void MoveFile() {
        setEstado( estado.MoveFile());
    }

    public void ChangeWorkingDirectory() {
        setEstado( estado.ChangeWorkingDirectory());
    }

    public void GetWorkingDirContent() {
        setEstado( estado.GetWorkingDirContent());
    }

    public void GetWorkingDirPath() {
        setEstado( estado.GetWorkingDirPath());
    }

    public void GetFileContent() {
        setEstado( estado.GetFileContent());
    }

    public void RemoveFile() {
        setEstado( estado.RemoveFile());
    }

    public void MakeDir() {
        setEstado(estado.MakeDir());
    }

    public void escolha_do_servidor(int pos) {
        setEstado(estado.escolha_do_servidor(pos));
    }
    
    
    
}
