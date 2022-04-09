import javax.swing.table.DefaultTableCellRenderer;

public class derived extends base {
    public double x;
    @Override
    public void func(int y){
        // double x;
        func(x);
        System.out.println(x);
    }
}
