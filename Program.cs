using Intel.Dal;
using System;
using System.Text;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Net; //For our IPAddress
using System.Net.Sockets; //For our TcpClient
using System.Text.RegularExpressions;


namespace passwordVaultHost
{

    class Program
    {
        static void Main(string[] args)
        {
            IPAddress ipAddress = IPAddress.Parse("127.0.0.1"); // Use your desired IP address
            int port = 8080; // Use your desired port number
            TcpListener listener = new TcpListener(ipAddress, port);
            listener.Start();
            Console.WriteLine("Accepting a client");
            TcpClient client1 = listener.AcceptTcpClient();
           
            NetworkStream ns = client1.GetStream();

            
            ns.Write(UTF32Encoding.UTF8.GetBytes("11"), 0, 2);

            TcpClient client = new TcpClient();

          





#if AMULET

            Jhi.DisableDllValidation = true;
#endif

            Jhi jhi = Jhi.Instance;
            JhiSession session;

            // This is the UUID of this Trusted Application (TA).
            //The UUID is the same value as the applet.id field in the Intel(R) DAL Trusted Application manifest.
            string appletID = "71a3832b-b824-4fee-8cf4-ee09b236441b";
            // This is the path to the Intel Intel(R) DAL Trusted Application .dalp file that was created by the Intel(R) DAL Eclipse plug-in.
            string appletPath = "C:/Users/csfwn/eclipse-workspace\\passwordVault\\bin\\passwordVault.dalp";

            // Install the Trusted Application
            Console.WriteLine("Installing the applet.");
            jhi.Install(appletID, appletPath);

            // Start a session with the Trusted Application
            byte[] initBuffer = new byte[] { }; // Data to send to the applet onInit function
            Console.WriteLine("Opening a session.");
            jhi.CreateSession(appletID, JHI_SESSION_FLAGS.None, initBuffer, out session);

            int responseCode;
            
            while (true)
            {


                //byte[] re = new byte[1028];
                //int br = ns.Read(re, 0, 500);
                ASCIIEncoding encoder = new ASCIIEncoding();
                //byte[] mc = new byte[br - 1];
                //Array.Copy(re, 1, mc, 0, br - 1);
                //Console.WriteLine(encoder.GetString(mc, 0, br - 1));

                byte[] inMessage = new byte[51];
                int bytesRead = 0;
                bytesRead = ns.Read(inMessage, 0, 51);
                
                byte[] recvBuff = new byte[100];
                switch (Convert.ToInt32(inMessage[0]))
                {
                    
                    case 1://register
                        byte[] sendBuff1 = new byte[20];
                        Array.Copy(inMessage, 1, sendBuff1, 0, bytesRead - 1);
                        jhi.SendAndRecv2(session, 1, sendBuff1, ref recvBuff, out responseCode);
                        if (responseCode == 1)
                        {
                            Console.Out.WriteLine("Registered successfuly");
                            byte[] send = new byte[2];
                            send[0] = Convert.ToByte(1);//case 1 in ascii value
                            send[1] = Convert.ToByte(1);//success
                            
                            ns.Write(send, 0, send.Length);
                        }
                        else
                        {
                            Console.Out.WriteLine("Registration not successful");
                            byte[] failed1 = new byte[2];
                            failed1[0] = Convert.ToByte(1);//case
                            failed1[1] = Convert.ToByte(0);//success

                            ns.Write(failed1, 0, failed1.Length);
                        }
                        break;
                    case 2://login
                        byte[] sendBuff2 = new byte[20];
                        Array.Copy(inMessage, 1, sendBuff2, 0, bytesRead - 1);
                        jhi.SendAndRecv2(session, 2, sendBuff2, ref recvBuff, out responseCode);
                        if(responseCode == 1)
                        {
                            Console.Out.WriteLine("Login successful");
                            byte[] send = new byte[2];
                            send[0] = Convert.ToByte(2);//case
                            send[1] = Convert.ToByte(1);//success

                            ns.Write(send, 0, send.Length);
                        }
                        else
                        {
                            Console.Out.WriteLine("login not successful");
                            byte[] failed1 = new byte[2];
                            failed1[0] = Convert.ToByte(2);//case
                            failed1[1] = Convert.ToByte(0);//success

                            ns.Write(failed1, 0, failed1.Length);
                        }
                        break;
                    case 3://save passwd
                        byte[] sendBuff3 = new byte[50];
                        Array.Copy(inMessage, 1, sendBuff3, 0, bytesRead - 1);
                        jhi.SendAndRecv2(session, 3, sendBuff3, ref recvBuff, out responseCode);
                        if (responseCode == 1)
                        {
                            Console.Out.WriteLine("saved is successful");
                            byte[] send = new byte[2];
                            send[0] = Convert.ToByte(3);//case
                            send[1] = Convert.ToByte(1);//success

                            ns.Write(send, 0, send.Length);
                        }
                        else
                        {
                            Console.Out.WriteLine("save is not successful");
                            byte[] failed1 = new byte[2];
                            failed1[0] = Convert.ToByte(3);
                            failed1[1] = Convert.ToByte(0);//success

                            ns.Write(failed1, 0, failed1.Length);
                        }
                        break;
                    case 4://retreieve passwd
                        byte[] sendBuff4 = new byte[10];
                        Array.Copy(inMessage, 1, sendBuff4, 0, bytesRead - 1);
                        jhi.SendAndRecv2(session, 4, sendBuff4, ref recvBuff, out responseCode);
                        if(responseCode == 1)
                        {
                            Console.Out.WriteLine("retreived password, now sending to user");
                            byte[] send1 = new byte[52];
                            send1[0] = Convert.ToByte(4);
                            send1[1] = Convert.ToByte(1);
                            Array.Copy(recvBuff, 0, send1, 2, 50);
                            ns.Write(send1, 0, send1.Length);
                        }
                        else
                        {
                            byte[] send1 = new byte[2];
                            send1[0] = Convert.ToByte(4);
                            send1[1] = Convert.ToByte(0);
                            ns.Write(send1, 0, send1.Length);
                        }
                        Console.Out.WriteLine("info retreived is: " + UTF32Encoding.UTF8.GetString(recvBuff));
                        break;
                    case 5://logout
                        byte[] garbage = UTF32Encoding.UTF8.GetBytes("Hello");
                        jhi.SendAndRecv2(session, 5, garbage, ref recvBuff, out responseCode);
                        byte[] send5 = new byte[2];
                        send5[0] = Convert.ToByte(5);
                        send5[1] = Convert.ToByte(1);
                        ns.Write(send5, 0, send5.Length);
                        break;
                    case 6:
                        byte[] garbage1 = UTF32Encoding.UTF8.GetBytes("Hello");
                        jhi.SendAndRecv2(session, 6, garbage1, ref recvBuff, out responseCode);
                        break;
                }

            }
            // Close the session
            Console.WriteLine("Closing the session.");
            jhi.CloseSession(session);

            //Uninstall the Trusted Application
            Console.WriteLine("Uninstalling the applet.");
            jhi.Uninstall(appletID);
            //client.Close();// close the socket;
            Console.WriteLine("Press Enter to finish.");
            Console.Read();
        }
    }
}