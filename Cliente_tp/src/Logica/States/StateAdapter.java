

package Logica.States;

import Logica.ClienteDados;


public abstract class StateAdapter implements IStates
{
    
    private ClienteDados cliente_dados;


    public StateAdapter(ClienteDados cliente_dados) 
    {
        this.cliente_dados = cliente_dados;
    }

    public ClienteDados getCliente() 
    {
        return cliente_dados;
    }

    public void setCliente(ClienteDados cliente_dados) 
    {
        this.cliente_dados = cliente_dados;
    }
    
    

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    @Override
    public IStates MakeDir(){return this;}

    @Override
    public IStates RemoveFile(){return this;}

    @Override
    public IStates GetFileContent(){return this;}

    @Override
    public IStates GetWorkingDirPath(){return this;}

    @Override
    public IStates GetWorkingDirContent(){return this;}

    @Override
    public IStates ChangeWorkingDirectory(){return this;}

    @Override
    public IStates MoveFile(){return this;}

    @Override
    public IStates CopyFile(){return this;}

    @Override
    public IStates Logout(){return this;}

    @Override
    public IStates Login(String nome, String pass){return this;}

    @Override
    public IStates Register(String nome, String pass){return this;}

    @Override
    public  IStates escolha_do_servidor(int pos){return this;}
  
    
}
