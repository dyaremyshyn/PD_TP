/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicodirectoria_tp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class ServicoDirectoria_tp {

    //udp cliente
    public static final int MAX_SIZE = 10000; //a discutir
    public static final String REQUEST_SERVIDORES = "LISTASERVIDOR";
    public static final String REQUEST_CLIENTES = "LISTACLIENTES";
    public static final String REQUEST_MSG_GERAL = "MSG_GERAL"; //para um determinado grupo de utilizadores pertencentes ao mesmo servidor
    public static final String REQUEST_MSG_INDIVIDUAL = "MSG_INDIVIDUAL";

    //udp servidor
    public static final String REQUEST_VALIDACAONOME = "VALIDACAO";

    private DatagramSocket socket;
    private DatagramPacket packet; //para receber os pedidos e enviar as respostas
    private boolean debug;

    //array que irá guardar os servidores activos
    ArrayList<servidor> lista_de_servidores = new ArrayList<>();

    //quando um cliente faz o login com sucesso , o Sdirectoria é avisado e fica com o nome do cliente e o nome do servidor
    //associar o cliente a um grupo multicast depois para por falar em grupo 
    ArrayList<cliente_d> lista_de_clientes_log = new ArrayList<>();

    public ServicoDirectoria_tp(int listeningPort, boolean debug) throws SocketException {
        socket = null;
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket = new DatagramSocket(listeningPort);
        this.debug = debug;

        //para testes 
        /*lista_de_servidores.add(new servidor("omelete","0.0.0.0",5000));
        lista_de_servidores.add(new servidor("queijo","0.0.0.0",6000));
        lista_de_servidores.add(new servidor("chorico","0.0.0.0",9000));*/
        //lista_de_clientes_log.add(new cliente_d("10.10.10.10","5000","omelete","Orlando"));
    }

    public String waitDatagram() throws IOException {
        String request;
        ObjectInputStream in;

        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject());
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            return null;
        }

        if (debug) {
            System.out.println("Recebido \"" + request + "\" de "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        }

        return request;

    }

    public void processRequests() {
        String receivedMsg;

        if (socket == null) {
            return;
        }

        if (debug) {
            System.out.println("UDP Serialized Server iniciado...");
        }

        while (true) {

            try {

                receivedMsg = waitDatagram();

                if (receivedMsg == null) {
                    continue;
                }

                if (receivedMsg.equalsIgnoreCase(REQUEST_SERVIDORES)) {

                    envia_lista_servidores();

                } else if (receivedMsg.equalsIgnoreCase(REQUEST_CLIENTES)) {

                    //envia_lista_clientes();
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_MSG_GERAL)) {

                    //continuar...
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_MSG_INDIVIDUAL)) {

                    //continuar...
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_VALIDACAONOME)) {

                    envia_resposta_de_validacao();

                } else {
                    continue;
                }

            } catch (IOException e) {
                System.out.println(e);
            }

        }

    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        int listeningPort;
        ServicoDirectoria_tp timeServer = null;

        if (args.length != 1) {
            System.out.println("Sintaxe: java UdpTimeServer listeningPort");
            return;
        }

        try {

            listeningPort = Integer.parseInt(args[0]);
            timeServer = new ServicoDirectoria_tp(listeningPort, true);
            timeServer.processRequests();

        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t" + e);
        } finally {
            if (timeServer != null) {
                timeServer.closeSocket();
            }
        }
    }

    public String[] recebenome() throws IOException {
        String request;
        ObjectInputStream in;
        String info_request[];

        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject()); //recebe uma string com o nome e o port

            info_request = request.split(" "); //o nome é a primeira string

            request = info_request[0];

        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            return null;
        }

        if (debug) {
            System.out.println("Recebi o nome: \"" + request + "\" de "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        }

        return info_request;

    }

    public void envia_resposta_de_validacao() throws IOException {
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        bOut = new ByteArrayOutputStream(MAX_SIZE);
        out = new ObjectOutputStream(bOut);

        String[] nomeEPort = recebenome();

        String nome = nomeEPort[0];

        boolean existeNome = verifica_servidor_repetido(nome);

        out.writeObject(existeNome);

        packet.setData(bOut.toByteArray());
        packet.setLength(bOut.size());
        socket.send(packet);

        if (!existeNome) //caso nao exista ele adiciona o servidor logo 
        {
            lista_de_servidores.add(new servidor(nome, packet.getAddress().getHostAddress(), Integer.parseInt(nomeEPort[1])));
        }

    }

    public boolean verifica_servidor_repetido(String nome1) {

        for (int i = 0; i < lista_de_servidores.size(); i++) {
            String info_servidor = lista_de_servidores.get(i).toString();
            //queremos o nome que é a primeira string
            String[] dados = info_servidor.split(" ");
            //cria objeto                     
            if (dados[0].equals(nome1)) {
                return true;
            }
        }

        return false;
    }

    public void envia_lista_servidores() throws IOException {
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        bOut = new ByteArrayOutputStream(MAX_SIZE);
        out = new ObjectOutputStream(bOut);

        //envia o numero de sevidores que o cliente vai receber
        out.writeObject(lista_de_servidores.size());

        packet.setData(bOut.toByteArray());
        packet.setLength(bOut.size());
        socket.send(packet);

//O ip e porto de destino já se encontram definidos em packet
        for (int i = 0; i < lista_de_servidores.size(); i++) {
//envia os servidores 
            bOut = new ByteArrayOutputStream(MAX_SIZE);
            out = new ObjectOutputStream(bOut);

            String info = lista_de_servidores.get(i).toString();
            out.writeObject(info);
            out.flush();

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());
            socket.send(packet);
        }
        //System.out.println("Tamanho da resposta serializada:  "+bOut.size()+" bytes");

    }

}
