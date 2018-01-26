package kyle.peaktracker;

public class Peak {

    private int _id;
    private String _name;
    private int _height;
    private String _climbed;
    private String _date;
    private String _list;

    //Constructors
    public Peak(){
    }

    public Peak(String _name){
        this._name = _name;
    }

    //Setters
    public void set_id(int _id){
        this._id = _id;
    }

    public void set_name(String _name){
        this._name = _name;
    }

    public void set_height(int _height){
        this._height = _height;
    }

    public void set_climbed(String _climbed){
        this._climbed = _climbed;
    }

    public void set_date(String _date){
        this._date = _date;
    }

    public void set_list(String _list){
        this._list = _list;
    }

    //Getters
    public int get_id(){
        return _id;
    }

    public String get_name(){
        return _name;
    }

    public int get_height(){
        return _height;
    }

    public String get_climbed(){
        return _climbed;
    }

    public String get_date(){
        return _date;
    }

    public String get_list(){
        return _list;
    }

    //Methods

    public String toString(){
        String printPeak = _id + ": " + _name + " | Climbed: " + _climbed;
        return printPeak;
    }
}
