
package Logica.States;


public interface IStates {
    
    IStates escolha_do_servidor(int pos);
    IStates Register(String nome, String pass);
    IStates Login(String nome, String pass);
    IStates Logout();
    IStates CopyFile();
    IStates MoveFile();
    IStates ChangeWorkingDirectory();
    IStates GetWorkingDirContent();
    IStates GetWorkingDirPath();
    IStates GetFileContent();
    IStates RemoveFile(); //elimina directorias tambem
    IStates MakeDir();  
    
}
