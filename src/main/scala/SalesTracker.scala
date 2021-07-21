import scala.swing._
import javax.swing.ImageIcon
import java.io._
import scala.io.Source
import event._
import scala.util.control.Breaks._

class gui extends SimpleSwingApplication{
    def top = new MainFrame{
        title = "SalesTracker"
        var itemTable = new Table(16, 4)
        def headerTable{    
            itemTable.showGrid = false
            itemTable.update(0,0,"ID")
            itemTable.update(0,1,"NAME")
            itemTable.update(0,2,"PRICE")
            itemTable.update(0,3,"INSTORE")
        }
        def updateTable{
            for(i<-0 until SalesTracker.itemTotal){
                itemTable.update(i+1, 0, i)
                itemTable.update(i+1, 1, SalesTracker.itemName(i))
                itemTable.update(i+1, 2, SalesTracker.itemPrice(i))
                itemTable.update(i+1, 3, SalesTracker.itemCount(i))
            }
            repaint
        }
        def clearTable{
            for(i<-0 until SalesTracker.itemTotal+1){
                itemTable.update(i+1, 0, null)
                itemTable.update(i+1, 1, null)
                itemTable.update(i+1, 2, null)
                itemTable.update(i+1, 3, null)
            }
            repaint
        }
        
        //refresh button: reset table, reload file
        def refreshing{
            clearTable
            SalesTracker.clearAll
            SalesTracker.loadFile
            updateTable
        }
        //add button: add new item
        def adding(name:String, price:Double, count:Int){
            for(i<- 0 until SalesTracker.itemTotal){
                if(name.toUpperCase == SalesTracker.itemName(i)){
                    Dialog.showMessage(contents.head, "Item already exists", "Error")
                    break
                }
            }
            SalesTracker.newItem(name.toUpperCase(), price, count)
            updateTable
        }
        //update button: update count
        def updating(name:String, count:Int){
            try SalesTracker.updateCount(name.toUpperCase, count)
            catch {
                case e: NumberFormatException => Dialog.showMessage(contents.head, "Item does not Exist","Error")
            }
            updateTable
        }
        //edit button: edit price
        def editing(name:String, price:Double){
            try SalesTracker.updatePrice(name.toUpperCase, price)
            //catch {case e: Exception => println(e)}
            catch {
                case e: NumberFormatException => Dialog.showMessage(contents.head, "Item does not Exist","Error")
            }
            updateTable
        }

        //delete button: set a column to null
        def deleting(id:Int){
            itemTable.update(id, 0, null)
            itemTable.update(id, 1, null)
            itemTable.update(id, 2, null)
            itemTable.update(id, 3, null)
            repaint
            SalesTracker.itemTotal-=1
        }

        //commit button: store table to file, display commmited confirmation
        def committing{
            val writer = new PrintWriter(new File("src/main/resources/salesTracker.txt"))
            var total = SalesTracker.itemTotal
            var iterator = 0;
            for(i<-0 until total){
                iterator+=1
                if(itemTable(iterator,0) != null){
                    writer.write(itemTable(iterator,1) + ", ")
                    writer.write(itemTable(iterator,2) + ", ")
                    writer.write(itemTable(iterator,3) + "\n")
                }
                else{
                    iterator+=1;
                    writer.write(itemTable(iterator,1) + ", ")
                    writer.write(itemTable(iterator,2) + ", ")
                    writer.write(itemTable(iterator,3) + "\n")
                }
            }
            writer.close()
            Dialog.showMessage(contents.head, "Commit successed!", "Commit")
        }

        headerTable
        updateTable
        
        contents = new BoxPanel(Orientation.Vertical){
            contents += new BoxPanel(Orientation.Horizontal) {
                contents+= itemTable
                contents+= new BoxPanel(Orientation.Vertical){
                    val refresh = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    refresh.icon = new ImageIcon(getClass().getResource("/refresh.png"))
                    contents+= refresh
                    contents+= Swing.VStrut(5)

                    val add = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    add.icon = new ImageIcon(getClass().getResource("/add.png"))
                    contents+= add
                    contents+= Swing.VStrut(5)

                    val update = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    update.icon = new ImageIcon(getClass().getResource("/update.png"))
                    contents+= update
                    contents+= Swing.VStrut(5)

                    val edit = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    edit.icon = new ImageIcon(getClass().getResource("/edit.png"))
                    contents+= edit
                    contents+= Swing.VStrut(5)

                    val delete = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    delete.icon = new ImageIcon(getClass().getResource("/delete.png"))
                    contents+= delete
                    contents+= Swing.VStrut(5)


                    val commit = new Button(""){
                        minimumSize = new Dimension(36,36)
                        maximumSize = new Dimension(36,36)
                        preferredSize = new Dimension(36,36)
                    }
                    commit.icon = new ImageIcon(getClass().getResource("/commit.png"))
                    contents+= commit
                    contents+= Swing.VStrut(5)

                    border = Swing.EmptyBorder(10, 10, 10, 10)

                    listenTo(refresh)
                    listenTo(add)
                    listenTo(update)
                    listenTo(edit)
                    listenTo(delete)
                    listenTo(commit)
                    reactions +={
                        case ButtonClicked(component) if component == refresh =>
                            refreshing
                        case ButtonClicked(component) if component == add =>
                            val result = Dialog.showInput(contents.head, "Add new Items", "New", initial="ITEM, 1.00, 1")
                            val r = result.getOrElse("").toString.split(", ")
                            adding(r(0),r(1).toDouble,r(2).toInt)
                        case ButtonClicked(component) if component == update =>
                            val result = Dialog.showInput(contents.head, "Update Count", "Amount", initial="ITEM, 1")
                            val r = result.getOrElse("").toString.split(", ")
                            updating(r(0), r(1).toInt)
                        case ButtonClicked(component) if component == edit =>
                            val result = Dialog.showInput(contents.head, "Update Count", "Amount", initial="ITEM, 1.00")
                            val r = result.getOrElse("").toString.split(", ")
                            editing(r(0),r(1).toDouble)
                        case ButtonClicked(component) if component == delete =>
                            val result = Dialog.showInput(contents.head, "Remove an Items", "Delete", initial="ITEM")
                            var id = SalesTracker.itemName.find(_._2 == result.getOrElse("").toString.toUpperCase()).map(_._1).getOrElse("").toString.toInt
                            deleting(id+1)
                        case ButtonClicked(component) if component == commit =>
                            committing                        
                    }
                }
            }
            border = Swing.EmptyBorder(10, 10, 10, 10)
        }
    }
}

object SalesTracker {
    var itemTotal:Int = 0
    var itemName = scala.collection.mutable.Map[Int,String]()
    var itemPrice = scala.collection.mutable.Map[Int,String]()
    var itemCount = scala.collection.mutable.Map[Int,String]()

    def newItem(name:String, price:Double, instore:Int){
        itemName += (itemTotal -> name)
        itemPrice += (itemTotal -> f"$price%1.2f")
        itemCount += (itemTotal -> instore.toString)
        itemTotal+=1
    }
    def updatePrice(id:Int, price:Double){
        itemPrice(id) = f"$price%1.2f"
    }
    def updatePrice(name:String, price:Double){
        var id = itemName.find(_._2 == name).map(_._1).getOrElse("")
        updatePrice(id.toString.toInt, price)
    }
    def updateCount(id:Int, instore:Int){
        itemCount(id) = instore.toString
    }
    def updateCount(name:String, instore:Int){
        var id = itemName.find(_._2 == name).map(_._1).getOrElse("")
        updateCount(id.toString.toInt, instore)
    }
    def printTable{
        println("ITEM ID \tITEM NAME: \tITEM PRICE: \tITEM COUNT: ")
        for(i<-0 until itemTotal) println(i + "\t\t" + itemName(i) + "\t\t"+ itemPrice(i) + "\t\t"+ itemCount(i))            
    }
    def loadFile{
        val file = "src/main/resources/salesTracker.txt"
        for (line <- Source.fromFile(file).getLines) {
            val token = line.split(", ")
            newItem(token(0).toString,token(1).toDouble,token(2).toInt)
        }
    }
    def clearAll{
        itemTotal = 0
        itemName.clear()
        itemPrice.clear()
        itemCount.clear()
    }
    def main(args: Array[String]): Unit = {
        loadFile
        val ui = new gui
        ui.top.visible = true
    }
}