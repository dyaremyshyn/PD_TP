/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor_tp;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio & Dmytro
 */
class AtendeCliente extends Thread {

    Socket socketToClient;
    int myId;
    int fez_login;
    int idCliente;
    
    
    
    ArrayList<cliente> lista_cli;

    public AtendeCliente(Socket s, ArrayList<cliente> lista_clientes, int id) {
        socketToClient = s;
        myId = id;
        lista_cli = lista_clientes;
        System.out.println("[SERVIDOR] estabeleci ligação com um cliente com sucesso !...");

    }

    @Override
    public void run() {
        String request, resposta;
        String nomePasta;
        boolean cli_on=true;
        BufferedReader in;
        PrintWriter out;

        try {

            while (cli_on) { //mudar para o request sair quebrar o while

                out = new PrintWriter(socketToClient.getOutputStream());
                in = new BufferedReader(new InputStreamReader(socketToClient.getInputStream()));

                request = in.readLine();

                if (request == null) { //EOF
                    socketToClient.close();
                    System.out.println("<Thread_" + myId + "> "
                            + "Ligacacao encerrada.");
                    return;
                }

                System.out.println("<Thread_" + myId
                        + "> Recebido \"" + request.trim() + "\" de "
                        + socketToClient.getInetAddress().getHostAddress() + ":"
                        + socketToClient.getPort());

                if (request.equalsIgnoreCase(Servidor_tp.REQUEST_LOGIN)) {
                    System.out.println("[SERVIDOR] recebi um pedido de login");
                    //recebe nome e pass
                    String nomeEpass = in.readLine();
                    System.out.println("[SERVIDOR] cliente: " + nomeEpass);
                    //Constroi a resposta terminando-a com uma mudanca de linha              
                    //Envia a resposta ao cliente
                    boolean var = login_efectuado_com_sucesso(nomeEpass);
                    out.println(var);
                    out.flush();

                    lista_cli.get(idCliente).setLoginEfetuado(var);
                    System.out.println("[SERVIDOR] login -> " + var);
                } else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_REGISTAR)) {
                    System.out.println("[SERVIDOR] recebi um pedido de registo");

                    //recebe nome e pass
                    String nomeEpass = in.readLine();
                //Constroi a resposta terminando-a com uma mudanca de linha              
                    //Envia a resposta ao cliente
                    out.println(registo_efectuado_com_sucesso(nomeEpass));
                    out.flush();
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_LOGOUT)) {
                    System.out.println("[SERVIDOR] cliente fez logout");
                    cli_on = false;
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_MAKEDIR)) {
                    System.out.println("[SERVIDOR] recebi um pedido de makedir");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        out.println(criacao_de_pasta(nomePasta));
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_GETWORKINGDIRCONTENT)) {
                    System.out.println("[SERVIDOR] recebi um pedido de conteudo da pasta");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        out.println(getConteudo());
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_REMOVEFILE)) {
                    System.out.println("[SERVIDOR] recebi um pedido de remover pasta");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        deleteDir(nomePasta);
                        out.println(true);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_CHANGEWORKINGDIRECTORY)) {
                    System.out.println("[SERVIDOR] recebi um pedido de mudar pasta de trabalho");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        out.println(mudarPastaTra(nomePasta));
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_COPYFILE)) {
                    System.out.println("[SERVIDOR] recebi um pedido para copiar ficherio");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        String nomeFich = in.readLine();
                        out.println(mover_ficheiro(nomePasta,nomeFich));
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_MOVEFILE)) {
                    System.out.println("[SERVIDOR] recebi um pedido para mover ficherio");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        out.println("não implementado");
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }else if (request.equalsIgnoreCase(Servidor_tp.REQUEST_GETFILECONTENT)) {
                    System.out.println("[SERVIDOR] recebi um pedido para mostrar conteudo do ficherio");
                    if(lista_cli.get(idCliente).getLoginEfetuado()){
                        nomePasta = in.readLine();
                        out.println("não implementado");
                        out.flush();
                    }else{
                        out.println(false);
                        out.flush();
                    }
                }

                //FALTA ADICIONAR:       
                //  - "COPYFILE";
                
            }

        } catch (IOException e) {
            System.out.println("<Thread_" + myId + "> Erro na comunicação como o cliente "
                    + socketToClient.getInetAddress().getHostAddress() + ":"
                    + socketToClient.getPort() + "\n\t" + e);
        } finally {
            try {
                socketToClient.close();
            } catch (IOException e) {
            }
        }
    }
    
    public boolean mover_ficheiro(String nomeP, String nomeF){
        return lista_cli.get(idCliente).getAmbienteTrabalho().moverFicheiro(nomeP, nomeF);
    }
    
    public boolean mudarPastaTra(String nome){
        return lista_cli.get(idCliente).mudarPastaTrabalho(nome); 
    }
    public void deleteDir(String nomePasta){
        lista_cli.get(idCliente).getAmbienteTrabalho().delDir(nomePasta);
    }
    
    public String getConteudo(){
        return lista_cli.get(idCliente).getAmbienteTrabalho().listarConteudoPastaAtual();
    }

    public boolean criacao_de_pasta(String nomePasta){
        lista_cli.get(idCliente).getAmbienteTrabalho().addDir(new Pasta(nomePasta));
        return true;
    }
    
    public boolean login_efectuado_com_sucesso(String nomeEpass) {
        String nome;
        String pass;

        String[] info_cli = nomeEpass.split(" ");

        if (info_cli[0] != null) {
            nome = info_cli[0];
        } else {
            return false;
        }

        if (info_cli[1] != null) {
            pass = info_cli[1];
        } else {
            return false;
        }

        //caso o utilizador seja repetido é porque os dados estao certos
        if (utilizador_repetido(nome, pass, 1)) {
            System.out.println("[REGISTO] O cliente: " + nome + " com pass: " + pass + " login com sucesso");
            return true;
        } else {
            System.out.println("[REGISTO] O cliente: " + nome + " com pass: " + pass + " login sem sucesso");
        }

        return false;
    }

    public boolean logout_efectuado_com_sucesso(String nomeEpass) {

        String nome;
        String pass;

        String[] info_cli = nomeEpass.split(" ");

        if (info_cli[0] != null) {
            nome = info_cli[0];
        } else {
            return false;
        }

        if (info_cli[1] != null) {
            pass = info_cli[1];
        } else {
            return false;
        }

        cliente cli = new cliente(nome, pass);
        int index = procuraCliente(cli);
        if (index != -1) {
            lista_cli.remove(index);
            return true;
        }
        return false;
    }

    public int procuraCliente(cliente c) {
        for (int i = 0; i < lista_cli.size(); i++) {
            if (lista_cli.get(i).getLog_nome().equals(c.getLog_nome())) {
                if (lista_cli.get(i).getPassword().equals(c.getPassword())) {
                    
                    return i; //caso nome e pass coencidam entao faz login com sucesso
                }
            }
        }
        return -1;
    }

    public boolean registo_efectuado_com_sucesso(String nomeEpass) {

        String nome;
        String pass;

        String[] info_cli = nomeEpass.split(" ");

        if (info_cli[0] != null) {
            nome = info_cli[0];
        } else {
            return false;
        }

        if (info_cli[1] != null) {
            pass = info_cli[1];
        } else {
            return false;
        }

        if (!utilizador_repetido(nome, pass, 0)) {
            cliente cli = new cliente(nome, pass);
            lista_cli.add(cli);
            System.out.println("[REGISTO] O cliente: " + nome + " com pass: " + pass + " registou-se com sucesso");
            return true;
        } else {
            System.out.println("[REGISTO] O cliente: " + nome + " com pass: " + pass + " registo sem sucesso");
        }

        return false;

    }

    public boolean utilizador_repetido(String nome, String pass, int tipoVerificacao) {

        //tipoverificacao serve para distiniguir as normas de comparaçao que dizem se um utilizador é repetido ou nao
        // caso seja 1 nome e pass serao verificadas
        //caso seja 0 só sera verificado o nome
        if (tipoVerificacao == 1) {
            for (int i = 0; i < lista_cli.size(); i++) { //percorre o array todo e compara com a info que ja contem
                if (lista_cli.get(i).getLog_nome().equals(nome)) {
                    if (lista_cli.get(i).getPassword().equals(pass)) {
                        idCliente=i;
                        return true; //caso nome e pass coencidam entao faz login com sucesso
                    }
                }
            }
        } else if (tipoVerificacao == 0) {
            for (int i = 0; i < lista_cli.size(); i++) { //percorre o array todo e compara com a info que ja contem
                if (lista_cli.get(i).getLog_nome().equals(nome)) {
                    return true; //caso nome e pass coencidam entao faz login com sucesso
                }
            }
        }

        return false;
    }
}

public class Servidor_tp {

    public static final int MAX_SIZE = 1025;
    public static final int TIMEOUT = 10; //segundos

    //UDP serviço de directoria
    public static final String REQUEST_VALIDACAONOME = "VALIDACAO";
    public static final String REQUEST_ADDUPDATESERVIDOR = "UPDATESERVERLIST"; //heartbeat
    public final static long SERVERSIGNAL_SEND_RATE = 30000;	//30 segs
    //TCP cliente
    public static final String REQUEST_LOGIN = "LOGIN";
    public static final String REQUEST_REGISTAR = "REGISTAR";
    
    public static final String REQUEST_LOGOUT = "LOGOUT";
    public static final String REQUEST_CAMINHO = "CAMINHO";
    public static final String REQUEST_COPYFILE = "COPYFILE";
    public static final String REQUEST_MOVEFILE = "MOVEFILE";
    public static final String REQUEST_CHANGEWORKINGDIRECTORY = "CHANGEWORKINGDIRECTORY";
    public static final String REQUEST_GETWORKINGDIRCONTENT = "GETWORKINGDIRCONTENT";
    public static final String REQUEST_REMOVEFILE = "REMOVEFILE";
    public static final String REQUEST_MAKEDIR = "MAKEDIR";
    public static final String REQUEST_GETFILECONTENT="GETFILECONTENT";
    //add outros requests necessarios NOTA add aqui e no cliente....

    public static int PORT;
    public static InetAddress IPSdirectoria;
    public static int PortSdirectoria;
    public static String Nome_serv;

    //heabeat
    Timer manda_sinal_para_SD;

    private final ServerSocket socket;

    public ArrayList<cliente> lista_clientes = new ArrayList<>();

    public Servidor_tp() throws IOException {
        //socket = new ServerSocket(PORT);
        socket = new ServerSocket(0); //atribui port automatico
        PORT = socket.getLocalPort();

        //adicionei 3 utilizadores pré-definidos (estas contas) estaram registadas em todos os servidores
        lista_clientes.add(new cliente("sergio", "1234"));
        lista_clientes.add(new cliente("dmytro", "1234"));
        lista_clientes.add(new cliente("adriano", "1234"));
    }

    public final void processRequests() throws IOException {
        int threadId = 1;
        Socket toClientSocket;

        if (socket == null) {
            return;
        }

        System.out.println("Concurrent TCP Server iniciado no porto " + socket.getLocalPort() + " ...");

        //inicia heartbeat
        manda_sinal_para_SD = new Timer();
        manda_sinal_para_SD.scheduleAtFixedRate(new HeartbeatTask(), 0, SERVERSIGNAL_SEND_RATE);

        while (true) {
            toClientSocket = socket.accept();  //espera por conecção     

            new AtendeCliente(toClientSocket, lista_clientes, threadId++).start(); // quando recebe um cliente cria uma tread dedicada para esse cliente            
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 3) {
            System.out.println("Sintaxe: java TPCServer: nome ip_doSdirect port_doSdirect");
            return;
        }
        String nome = args[0].trim(); // string com o nome do servidor

//validação do nome dados nos argumentos ao servidor
        if (!verifica_se_NomejaExiste(nome, InetAddress.getByName(args[1]), Integer.parseInt(args[2]))) { // ! caso seja falso com o ! ele entra no if 

            Servidor_tp tcpServer = new Servidor_tp();

            PortSdirectoria = Integer.parseInt(args[2]);
            IPSdirectoria = InetAddress.getByName(args[1]);
            Nome_serv = nome;

            tcpServer.processRequests();

            System.out.println("[SERVIDOR] criado com sucesso!");

        } else {
            System.out.println("[SERVIDOR] não foi possivel criar o sevidor");
            System.out.println("--> caso não tenha sido erro de socket é porque o nome do servidor já existe...");
        }

    }

    public static boolean verifica_se_NomejaExiste(String nomeEport, InetAddress serDirectoria_Addr, int serDirectoria_Port) throws IOException, ClassNotFoundException {
        //nesta função é enviada um pedido ao Serviço de directoria com o objetivo de verificar
        //se ja existe um servidor na lista dele com o nome atribuido a este servidor

        DatagramSocket socket_udp;
        ObjectOutputStream out;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);
        ObjectInputStream in;

        try {
            socket_udp = new DatagramSocket();
            socket_udp.setSoTimeout(TIMEOUT * 1000);
        } catch (SocketException ex) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + ex);
            return false;
        }

        //envia pedido de validação ao UDP serviço de directoria
        out.writeObject(REQUEST_VALIDACAONOME);
        out.flush();

        DatagramPacket packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

        //envia o nome que será validado
        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(nomeEport);
        out.flush();

        packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

        DatagramPacket packet_receve = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket_udp.receive(packet_receve);

        in = new ObjectInputStream(new ByteArrayInputStream(packet_receve.getData(), 0, packet_receve.getLength()));

        boolean resposta = (boolean) in.readObject();

        System.out.println("[SDrirect resposta] ha um nome igual -> " + resposta);

        return resposta;

    }

    public static void manda_info(String info_servidor, InetAddress serDirectoria_Addr, int serDirectoria_Port) throws IOException, ClassNotFoundException {

        DatagramSocket socket_udp;
        ObjectOutputStream out;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);
        ObjectInputStream in;

        try {
            socket_udp = new DatagramSocket();
            socket_udp.setSoTimeout(TIMEOUT * 1000);
        } catch (SocketException ex) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + ex);
            return;
        }

        //envia pedido de validação ao UDP serviço de directoria
        out.writeObject(REQUEST_ADDUPDATESERVIDOR);
        out.flush();

        DatagramPacket packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

        //envia o nome que será validado
        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(info_servidor);
        out.flush();

        packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

    }
    
    public static void move_File(){
    	
        
        InputStream inStream = null;
	OutputStream outStream = null;

    	try{

    	    File afile =new File("C:\\folderA\\Afile.txt");
    	    File bfile =new File("C:\\folderB\\Afile.txt");

    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);

    	    byte[] buffer = new byte[1024];

    	    int length;
    	    //copy the file content in bytes
    	    while ((length = inStream.read(buffer)) > 0){

    	    	outStream.write(buffer, 0, length);

    	    }

    	    inStream.close();
    	    outStream.close();

    	    //delete the original file
    	    afile.delete();

    	    System.out.println("File is copied successful!");

    	}catch(IOException e){
    	    e.printStackTrace();
    	} 
    }
    
    
    /**
     * class directoria
     */
    private class HeartbeatTask extends TimerTask {

        @Override
        public void run() {
            String info = Nome_serv + " " + PORT;
            try {
                //pede para guardar
                manda_info(info, IPSdirectoria, PortSdirectoria);
            } catch (IOException ex) {
                Logger.getLogger(Servidor_tp.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor_tp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
