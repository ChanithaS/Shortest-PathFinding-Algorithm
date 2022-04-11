public class Nodes {
    public int rowNo;            // row number of the node
    public int columnNo;         // column number of the node
    public Nodes parent;    // node object to store its parent node
    public String direction;

    public Nodes(int rowNumber, int columnNumber) {
        this.rowNo = rowNumber;
        this.columnNo = columnNumber;
    }
}
