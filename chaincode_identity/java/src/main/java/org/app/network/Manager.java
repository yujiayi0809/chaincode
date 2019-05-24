package org.app.network;

import org.app.chaincode.invocation.InvokeChaincode;
import org.app.chaincode.invocation.QueryChaincode;
import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.config.Config;
import org.app.user.UserContext;
import org.app.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Manager extends PeerInfo {

    public JFrame user_frame = new JFrame();

    public JButton createChannel_button = new JButton("创建通道");
    public JPanel joinPeers_panel = new JPanel();
    public JTextField joinPeers_text = new JTextField(20);
    public JButton joinPeers_button = new JButton("加入节点");

    public JPanel joinNewPeer_panel = new JPanel();
    public JTextField joinNewPeer_text = new JTextField(20);
    public JButton joinNewPeer_button = new JButton("新增节点");

    public JPanel deploy_instantiate_panel = new JPanel();
    public JButton deploy_button = new JButton("安装链码");
    public JButton instantiate_button = new JButton("实例化链码");

    public JButton invoke_button = new JButton("添加数据");
    public JFrame invoke_frame = new JFrame();

    public JPanel invoke_panel00 = new JPanel();
    public JPanel invoke_panel01 = new JPanel();
    public JPanel invoke_panel02 = new JPanel();
    public JPanel invoke_panel03 = new JPanel();
    public JPanel invoke_panel04 = new JPanel();
    public JPanel invoke_panel05 = new JPanel();

    public JLabel invoke_title = new JLabel("添加标识数据");
    public JLabel name_label = new JLabel("名称：");
    public JTextField name_text = new JTextField(20);
    public JLabel address_label = new JLabel("地址：");
    public JTextField address_text = new JTextField(20);
    public JLabel type_label = new JLabel("类型：");
    public JTextField type_text = new JTextField(20);
    public JLabel rootID_label = new JLabel("根节点号：");
    public JTextField rootID_text = new JTextField(18);
    public JButton add_button = new JButton("添加");

    public JButton query_button = new JButton("查询数据");
    public JFrame query_frame = new JFrame();

    public JPanel query_panel00 = new JPanel();
    public JPanel query_panel01 = new JPanel();
    public JPanel query_panel02 = new JPanel();
    public JPanel query_panel03 = new JPanel();

    public JLabel query_title = new JLabel("查询标识数据");
    public JLabel name_query_label = new JLabel("名称：");
    public JTextField name_query_text = new JTextField(20);
    public JButton q_button = new JButton("查询");
    public JTextArea info_text = new JTextArea(2,24);

    public UserContext org1Admin;
    public FabricClient fabClient;
    public Orderer orderer;
    public Channel mychannel;
    public static int peerNum;

    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    private String stringResponse;

    public Manager() {

        createChannel_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("创建通道...");
                createChannel();
                System.out.println("完成!!");
            }
        });
        joinPeers_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("加入节点...");
                joinPeers(Integer.parseInt(joinPeers_text.getText()));
                System.out.println("完成!!");
            }
        });
        joinNewPeer_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("新增节点...");
                joinNewPeer(Integer.parseInt(joinNewPeer_text.getText()));
                System.out.println("完成!!");
            }
        });
        deploy_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("安装链码...");
                deployChaincode();
                System.out.println("完成!!");

            }
        });
        instantiate_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("实例化链码...");
                instantiateChaincode();
                System.out.println("完成!!");
            }
        });
        invoke_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invoke_frame.setBounds(
                        new Rectangle(
                                (int) user_frame.getBounds().getX() + 50,
                                (int) user_frame.getBounds().getY() + 50,
                                (int) user_frame.getBounds().getWidth(),
                                (int) user_frame.getBounds().getHeight()
                        )
                );
                invoke_frame.setLayout(new GridLayout(6,1,10,10));
                invoke_panel00.add(invoke_title);
                invoke_panel01.add(name_label);
                invoke_panel01.add(name_text);
                invoke_panel02.add(address_label);
                invoke_panel02.add(address_text);
                invoke_panel03.add(type_label);
                invoke_panel03.add(type_text);
                invoke_panel04.add(rootID_label);
                invoke_panel04.add(rootID_text);
                invoke_panel05.add(add_button);
                invoke_frame.add(invoke_panel00);
                invoke_frame.add(invoke_panel01);
                invoke_frame.add(invoke_panel02);
                invoke_frame.add(invoke_panel03);
                invoke_frame.add(invoke_panel04);
                invoke_frame.add(invoke_panel05);
                invoke_frame.setVisible(true);
            }
        });
        add_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invoke("initIdentity", new String[]{name_text.getText(), address_text.getText(),
                        type_text.getText(),rootID_text.getText()});
            }
        });
        query_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query_frame.setBounds(
                        new Rectangle(
                                (int) user_frame.getBounds().getX() + 50,
                                (int) user_frame.getBounds().getY() + 50,
                                (int) user_frame.getBounds().getWidth(),
                                (int) user_frame.getBounds().getHeight()
                        )
                );
                query_frame.setLayout(new GridLayout(4,1,10,10));
                query_panel03.add(query_title);
                query_panel00.add(name_query_label);
                query_panel00.add(name_query_text);
                query_panel01.add(q_button);
                info_text.setLineWrap(true);
                info_text.setWrapStyleWord(true);
                query_panel02.add(new JScrollPane(info_text));
                query_frame.add(query_panel03);
                query_frame.add(query_panel00);
                query_frame.add(query_panel01);
                query_frame.add(query_panel02);
                query_frame.setVisible(true);
            }
        });
        q_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                query("quaryIdentityByName", new String[]{name_query_text.getText()});
                info_text.setText(stringResponse);
            }
        });

        user_frame.setLayout(new GridLayout(6,1,10,10));
        user_frame.add(createChannel_button);
        joinPeers_panel.add(joinPeers_text);
        joinPeers_panel.add(joinPeers_button);
        user_frame.add(joinPeers_panel);

        joinNewPeer_panel.add(joinNewPeer_text);
        joinNewPeer_panel.add(joinNewPeer_button);
        user_frame.add(joinNewPeer_panel);

        deploy_instantiate_panel.add(deploy_button);
        deploy_instantiate_panel.add(instantiate_button);
        user_frame.add(deploy_instantiate_panel);

        user_frame.add(invoke_button);
        user_frame.add(query_button);

        user_frame.setLocationRelativeTo(null);
        user_frame.pack();
        user_frame.setVisible(true);
        
        

        try {
            CryptoSuite.Factory.getCryptoSuite();
            Util.cleanUp();
            // Construct Channel
            org1Admin = new UserContext();
            File pkFolder1 = new File(Config.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder1 = new File(Config.ORG1_USR_ADMIN_CERT);
            File[] certFiles1 = certFolder1.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(Config.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    Config.ORG1_USR_ADMIN_CERT, certFiles1[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId(Config.ORG1_MSP);
            org1Admin.setName(Config.ADMIN);

            fabClient = new FabricClient(org1Admin);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createChannel(){
        try {
            // Create a new channel
            orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(Config.CHANNEL_CONFIG_PATH));

            byte[] channelConfigurationSignatures = fabClient.getInstance()
                    .getChannelConfigurationSignature(channelConfiguration, org1Admin);

            mychannel = fabClient.getInstance().newChannel(Config.CHANNEL_NAME, orderer, channelConfiguration,
                    channelConfigurationSignatures);

            Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, "Channel created "+mychannel.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinPeers(int y) {
        try {

            // Join Peers
            peerNum = y;
            for (int p = 0; p < y; p++) {
                peers_org1[p] = fabClient.getInstance().newPeer(org1_peers_name[p], org1_peers_url[p]);
                mychannel.joinPeer(peers_org1[p]);
            }
            mychannel.addOrderer(orderer);
            mychannel.initialize();

            Collection peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext())
            {
                Peer pr = (Peer) peerIter.next();
                Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO,pr.getName()+ " at " + pr.getUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void joinNewPeer(int x) {
        if(peerNum >= 5 || peerNum + x > 5) {
            System.out.println("节点数已满！");
        }
        else{
            try {
                for (int i = 0; i < x; i++) {
                    peerNum += 1;
                    peers_org1[peerNum - 1] = fabClient.getInstance().newPeer(org1_peers_name[peerNum - 1], org1_peers_url[peerNum - 1]);
                    mychannel.joinPeer(peers_org1[peerNum - 1]);
                }
                mychannel.initialize();

                Collection peers = mychannel.getPeers();
                Iterator peerIter = peers.iterator();
                while (peerIter.hasNext())
                {
                    Peer pr = (Peer) peerIter.next();
                    Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO,pr.getName()+ " at " + pr.getUrl());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deployChaincode() {
        try {
            List<Peer> org1Peers = new ArrayList<>();
            for (int p = 0; p < peerNum; p++) {
                org1Peers.add(peers_org1[p]);
            }

            mychannel.initialize();

            Collection<ProposalResponse> response = fabClient.deployChainCode(Config.CHAINCODE_1_NAME,
                    Config.CHAINCODE_1_PATH, Config.CHAINCODE_ROOT_DIR, TransactionRequest.Type.GO_LANG.toString(),
                    Config.CHAINCODE_1_VERSION, org1Peers);

            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        Config.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void instantiateChaincode(){
        try {

            ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            String[] arguments = { "" };
            Collection<ProposalResponse> response = channelClient.instantiateChainCode(Config.CHAINCODE_1_NAME, Config.CHAINCODE_1_VERSION,
                    Config.CHAINCODE_1_PATH, TransactionRequest.Type.GO_LANG.toString(), "init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        Config.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FabricClient fabClient_invoke;
    ChannelClient channelClient_invoke;
    boolean init_judge = true;
    public void invokeInit(){
        try {
            Util.cleanUp();
            String caUrl = Config.CA_ORG1_URL;
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(Config.ADMIN);
            adminUserContext.setAffiliation(Config.ORG1);
            adminUserContext.setMspId(Config.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            fabClient_invoke = new FabricClient(adminUserContext);

            channelClient_invoke = fabClient_invoke.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient_invoke.getChannel();
            Peer peer = fabClient_invoke.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
            EventHub eventHub = fabClient_invoke.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient_invoke.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            init_judge = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void invoke(String fcn, String[] args){
        try {
            if(init_judge) {
                invokeInit();
            }

            TransactionProposalRequest request = fabClient_invoke.getInstance().newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(Config.CHAINCODE_1_NAME).build();
            request.setChaincodeID(ccid);
            request.setFcn(fcn);
            String[] arguments = args;
            request.setArgs(arguments);
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8));
            tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
            request.setTransientMap(tm2);
            Collection<ProposalResponse> responses = channelClient_invoke.sendTransactionProposal(request);
            for (ProposalResponse res: responses) {
                ChaincodeResponse.Status status = res.getStatus();
                Logger.getLogger(InvokeChaincode.class.getName()).log(Level.INFO,"Invoked initIdentity on "+Config.CHAINCODE_1_NAME + ". Status - " + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void query(String fcn, String[] name){
        try {
            Util.cleanUp();
            String caUrl = Config.CA_ORG1_URL;
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(Config.ADMIN);
            adminUserContext.setAffiliation(Config.ORG1);
            adminUserContext.setMspId(Config.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(Config.ADMIN, Config.ADMIN_PASSWORD);

            FabricClient fabClient = new FabricClient(adminUserContext);

            ChannelClient channelClient = fabClient.createChannelClient(Config.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(Config.ORG1_PEER_0, Config.ORG1_PEER_0_URL);
            EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();


            String[] args1 = name;
            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for an identity by name - " + args1[0]);

            Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode("identity", fcn, args1);

            for (ProposalResponse pres : responses1Query) {
                stringResponse = new String(pres.getChaincodeActionResponsePayload());
                Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Manager m = new Manager();
//        m.createChannel();
//        System.out.print("请输入Peer节点数：");
//        Scanner first_in =new Scanner(System.in);
//        peerNum = first_in.nextInt();
//        m.joinPeers(peerNum);
//        System.out.print("是否添加节点:（是请输入1/否请输入2）");
//        Scanner second_in =new Scanner(System.in);
//        int judge1 = second_in.nextInt();
//        if(judge1 == 1){
//            m.joinNewPeer();
//        }
//        System.out.print("是否安装链码并实例化:（是请输入1/否请输入2）");
//        Scanner third_in =new Scanner(System.in);
//        int judge2 = third_in.nextInt();
//        if(judge2 == 1){
//            m.deployChaincode();
//            m.instantiateChaincode();
//        }
    }
}
