/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI_text;

import Logica.Cliente;
import Logica.States.Comandos;
import Logica.States.EscolhaServidor;
import Logica.States.IStates;
import Logica.States.Login_Registar;
import java.io.IOException;

import java.util.Scanner;

/**
 *
 * @author Sergio
 */
public class TextuserInterface {

    private Cliente cliente;
    private boolean sair = false;

    public TextuserInterface(Cliente cliente) {
        this.cliente = cliente;
    }

    public void iuEscolhaServidor() throws IOException, ClassNotFoundException {
        System.out.print("\n\nHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH\n");
        System.out.println("\n\n=== Lista de servidores ===\n");
        String lista = null;

        lista = cliente.pedido_lista_servidores();

        if (lista == null) {
            System.out.println("nenhum servidor encontrado\n");
        } else {
            System.out.println(lista);
        }

        System.out.print("\nEscolha: ");

        int c;
        Scanner sc = new Scanner(System.in);
        c = sc.nextInt();

        cliente.escolha_do_servidor(c);

    }

    public void iuLoginRegistar() {

        String nome = " ";
        String pass = " ";

        System.out.print("\n\nHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH\n");
        System.out.println("\n\n=== OPÇOES ===\n");
        System.out.println("1:Login");
        System.out.println("2:Registar");
        System.out.println("3:Sair");
        System.out.print("\n Escolha: ");

        int c;
        Scanner sc = new Scanner(System.in);
        c = sc.nextInt();

        if (c == 1) {
            System.out.println("HHHHH   LOGIN   HHHHH\n");
            System.out.print("-> nome: ");
            nome = sc.next();
            System.out.print("-> pass: ");
            pass = sc.next();
            //implementar verificações
            cliente.Login(nome, pass);
        } else if (c == 2) {
            System.out.println("HHHHH   REGISTAR   HHHHH\n");
            System.out.print("-> nome: ");
            nome = sc.next();
            System.out.print("-> pass: ");
            pass = sc.next();
            //implementar verificações
            cliente.Register(nome, pass);

        } else if (c == 3) {

            //sair
        } else {
            System.out.println("\n ## Opção não valida ##");
        }

    }

    public void iuComandos() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);

            String comando = " ";
            System.out.print("\n\nHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH\n");
            System.out.println("\n\n=== Introduza Comandos ===\n");
            System.out.print("-> comando: ");
            comando = sc.next();

            if (comando.equals("lstclientes")) {

                String lista = null;

                lista = cliente.pedido_lista_clientes();

                if (lista == null) {
                    System.out.println("nenhum servidor encontrado\n");
                } else {
                    System.out.println(lista);
                }

            } else if (comando.equals("chatg")) {
              
                String msg = sc.nextLine();
                
                
                cliente.msg_geral(msg);
            } else if (comando.equals("logout")) {
              
                
                cliente.Logout();
               
            } else if (comando.equals("help")) {
                 System.out.println("Comandos:\n"
                         + "lstclientes-> lista clientes : (modo de uso) lstclientes\n"
                         + "chatg-> envia mensagem para todos os clientes :(modo de uso) chatg msg\n"
                         + "logout-> cliente faz logout do servidor : (modo de uso) logout\n"
                         + "chati-> manda msg individual para o port x : (modo de uso) chati port msg\n"
                         + "moveFile-> move ficherio de uma pasta para outra: (modo de uso) movefile nomePasta nomeFicheiro"  
                         + "changeWorkingDirectory-> muda a pasta de trabalho: (modo de uso) changeWorkingDirectory nomePasta "
                         + "getWorkingDirContent-> recebe o conteudo da pasta de trabalho: (modo de uso) getWorkingDirContent"
                         + "getWorkingDirPath-> recebe o caminho da pasta de trabaalho: (modo de uso) getWorkingDirPath"
                         + "removeFile-> remove um ficheiro: (modo de uso) removeFile nomePasta"
                         + "makeDir-> Cria uma pasta e adiciona a pasta de trabalho: (modo de uso) makeDir nomePasta");
            }else if (comando.equals("chati")) {
                String port =  sc.next();
                String msg = sc.nextLine();               
                 
                 cliente.msg_individual(port,msg);
            } else if (comando.equals("...")) {
                //...
            } else if (comando.equals("...")) {
                //...
            } else if (comando.equals("...")) {
                //...
            } else if (comando.equals("...")) {
                //...
            } else {
                System.out.println("## COMANDO invalido##");
            }
            
            

        
    }

    public void corre() throws IOException, ClassNotFoundException {
        while (!sair) {
            IStates estado = cliente.getEstado();

            if (estado instanceof EscolhaServidor) {
                iuEscolhaServidor();
            } else if (estado instanceof Login_Registar) {
                iuLoginRegistar();
            } else if (estado instanceof Comandos) {
                iuComandos();
            }
        }
    }
    
    
    
}

