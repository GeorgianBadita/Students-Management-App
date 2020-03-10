package account;

import account.password.Password;
import domain.entities.Student;
import domain.validators.IllegalArgumentException;
import domain.validators.Validator;
import domain.validators.ValidatorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.AbstractRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;


public class AccountXMLRepository extends AbstractRepository<Account, String>{
    private String file;
    private DocumentBuilderFactory factory;

    /**
     * XML File Repository Account Constructor
     * @param v - Account Validator
     * @param fileName - path to the XML File
     */
    public AccountXMLRepository(Validator<Account> v, String fileName){
        super(v);
        this.file = fileName;
        this.factory = DocumentBuilderFactory.newInstance();
        try {
            readFromXML();
        } catch (ValidatorException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    /**
     * Function to read Accounts from XML File
     * @throws ValidatorException if the account is't well formed
     * @throws IllegalArgumentException - if parameters are null
     */
    private void readFromXML() throws ValidatorException, IllegalArgumentException {
        try {
            DocumentBuilder db = this.factory.newDocumentBuilder();
            Document doc = db.parse(new File(this.file));
            Element el = (Element) doc.getDocumentElement();

            NodeList nodeList = el.getElementsByTagName("account");

            for(int i = 0; i<nodeList.getLength(); i++){
                String username, accType, passString, saltString;
                Integer id, group;
                String name, email;
                Element item = (Element) nodeList.item(i);

                username = item.getAttribute("userName");

                NodeList nodeList1 = item.getElementsByTagName("accType");
                accType = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                nodeList1 = item.getElementsByTagName("passValue");

                passString = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                nodeList1 = item.getElementsByTagName("saltValue");

                saltString = nodeList1.item(0).getChildNodes().item(0).getNodeValue();

                Account acc = new Account(username, AccountType.valueOf(accType), new Password(passString, saltString));
                super.save(acc);
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Function to write in XML File
     */
    private void writeToXML(){
        try {
            DocumentBuilder db = this.factory.newDocumentBuilder();

            Document doc = db.newDocument();
            Element el = doc.createElement("accounts");

            doc.appendChild(el);

            for (Account acc:
                    super.findAll()) {
                el.appendChild(getAccount(doc, acc.getUsername(), acc.getAccType(), acc.getPassword().getPassValue(), acc.getPassword().getSaltValue()));
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            DOMSource ds = new DOMSource(doc);
            StreamResult sRes = new StreamResult(new File(this.file));
            Transformer transformer = tf.newTransformer();
            transformer.transform(ds, sRes);

        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get a new node to add into XML file
     */
    private static Node getAccount(Document doc, String userName, AccountType accountType, String passValue, String saltValue){
        Element account = doc.createElement("account");

        account.setAttribute("userName", userName);

        account.appendChild(getAccountElements(doc, "accType", accountType.toString()));

        account.appendChild(getAccountElements(doc, "passValue", passValue));

        account.appendChild(getAccountElements(doc, "saltValue", saltValue));

        return account;
    }

    /**
     * Utility method to create text node
     * @param doc - document type
     * @param name - property name
     * @param value - property value
     * @return a new node
     */
    private static Node getAccountElements(Document doc, String name, String value){
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    /**
     * Overriding save function for XML files
     * @param acc - Account to save
     * @return - null if the account is saved, the account otherwise
     * @throws ValidatorException - if the account is invalid
     * @throws IllegalArgumentException - if the account is null
     */
    @Override
    public Account save(Account acc) throws ValidatorException, IllegalArgumentException {
        Account saved = super.save(acc);

        if(saved == null){
            writeToXML();
            return null;
        }
        return saved;
    }

    /**
     * Overriding update function for XML files
     * @param acc - account to update
     * @return - null if the account was updated, the account otherwise
     * @throws ValidatorException - if the given account is invalid
     * @throws IllegalArgumentException - if some parameter is null
     */
    @Override
    public Account update(Account acc) throws ValidatorException, IllegalArgumentException {
        Account aux = super.update(acc);
        writeToXML();
        return aux;
    }


    /**
     * Overriding delete method for XML files
     * @param id - id of the account to be deleted
     * id must be not null
     * @return - the deleted account if it was deleted, null otherwise
     * @throws IllegalArgumentException - if id is null
     */
    @Override
    public Account delete(String id) throws IllegalArgumentException {
        Account aux = super.delete(id);
        writeToXML();
        return aux;
    }
}

