import scala.swing._
import javax.swing.ImageIcon


class gui extends SimpleSwingApplication{
    def top = new MainFrame{
        title = "SalesTracker";

        var itemTable = new Table(24, 4)
        itemTable.showGrid = false;
        itemTable.update(0,0,"ID");
        itemTable.update(0,1,"NAME");
        itemTable.update(0,2,"PRICE");
        itemTable.update(0,3,"INSTORE");
        
        def updateTable{
            for(i<-0 until SalesTracker.itemPrimary){
                itemTable.update(i+1, 0, i);
                itemTable.update(i+1, 1, SalesTracker.itemName(i));
                itemTable.update(i+1, 2, SalesTracker.itemPrice(i));
                itemTable.update(i+1, 3, SalesTracker.itemCount(i));
            }
        }

        contents = new BoxPanel(Orientation.Vertical){
            contents += new BoxPanel(Orientation.Horizontal) {
                contents+= itemTable;
                contents+= new BoxPanel(Orientation.Vertical){
                    val refresh = new Button(""){
                        minimumSize = new Dimension(36,36);
                        maximumSize = new Dimension(36,36);
                        preferredSize = new Dimension(36,36);
                        updateTable;
                    }
                    refresh.icon = new ImageIcon(getClass().getResource("/refresh.png"));
                    contents+= refresh;
                    contents+= Swing.VStrut(5);
                    contents+= new Button("Update"){
                        //update from what is filled in the table
                        minimumSize = new Dimension(36,36);
                        maximumSize = new Dimension(36,36);
                        preferredSize = new Dimension(36,36);
                    }
                    border = Swing.EmptyBorder(10, 10, 10, 10)
                }
            }
            contents += Swing.VStrut(5);
            contents += new BoxPanel(Orientation.Horizontal) {
                contents+= Button("Add a new Item"){
                    val result = Dialog.showInput(contents.head, "Add new Items", "New", initial="Item, Price, Amount");
                    val r = result.getOrElse("").toString.split(", ")

                    try{
                        SalesTracker.newItem(r(0).toUpperCase(), r(1).toDouble, r(2).toInt);
                    } catch{
                        case e: Throwable => Dialog.showMessage(contents.head, "INVALID INPUTS", "INVALID")
                    }

                    updateTable;
                }

                contents+= Swing.HStrut(5)
                
                contents+= Button("Update Amount"){
                    val result = Dialog.showInput(contents.head, "Update Count", "Amount", initial="Item, Amount");
                    val r = result.getOrElse("").toString.split(", ")

                    try{
                        SalesTracker.updateCount(r(0).toUpperCase(),r(1).toInt);
                    } catch{
                        case e: Throwable => Dialog.showMessage(contents.head, "INVALID INPUTS", "INVALID")
                    }
                    updateTable;
                }   
                
            }
            border = Swing.EmptyBorder(10, 10, 10, 10);
        }
    }
}



object SalesTracker {
    var itemPrimary:Int = 0;//primary key


    var itemName = scala.collection.mutable.Map[Int,String]();//itemName tracker
    var itemPrice = scala.collection.mutable.Map[Int,Double]();//price tracker
    var itemCount = scala.collection.mutable.Map[Int,Int]();//instore amount tracker

    case class Item(
        val itemName:String, 
        val price:Double, 
        var instore:Int)

    def newItem(name:String, price:Double, instore:Int){
        itemName += (itemPrimary -> name);
        itemPrice += (itemPrimary -> price);
        itemCount += (itemPrimary -> instore);
        itemPrimary+=1;

        printTable;
        println("\n\n")
    }

    def updatePrice(id:Int, price:Double){
        itemPrice(id) = price;
    }
    def updatePrice(name:String, price:Double){
        var id = itemName.find(_._2 == name).map(_._1).getOrElse("");
        itemPrice(id.toString.toInt) = price;
    }

    def updateCount(id:Int, instore:Int){
        itemCount(id) = instore;
    }
    def updateCount(name:String, instore:Int){
        var id = itemName.find(_._2 == name).map(_._1).getOrElse("");
        itemCount(id.toString.toInt) = instore;
    }


    def printTable{
        println("ITEM ID \tITEM NAME: \tITEM PRICE: \tITEM COUNT: ");
        for(i<-0 until itemPrimary)
            println(i + "\t\t" 
                    + itemName(i) + "\t\t"
                    + itemPrice(i) + "\t\t"
                    + itemCount(i))
                    
    }
    
    
    def printSummary{

    }

    def main(args: Array[String]): Unit = {
        newItem("APPLE", 0.3, 100);
        newItem("PEAR", 0.5, 50);
        newItem("ORANGE", 0.7, 120);


        val ui = new gui;
        ui.top.visible = true;
    }
}