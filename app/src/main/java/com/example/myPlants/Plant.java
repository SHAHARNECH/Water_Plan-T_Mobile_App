package com.example.myPlants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * The type Plant.
 */
public class Plant implements Comparable{
    private int id;
    private String plant_name;
    private String description;
    private int irrigation;
    private String plant_birthdate;
    private String family, nextIrr, image;

    /**
     * Instantiates a new Plant.
     *
     * @param name        the name
     * @param family      the family
     * @param description the description
     * @param irrigation  the irrigation days
     * @param date        the planted date
     * @param next        the next watering date
     * @param image       the image path
     */
    public Plant(String name, String family,String description, int irrigation, String date, String next, String image) {
        this.plant_name = name;
        this.family = family;
        this.description = description;
        this.irrigation = irrigation;
        this.plant_birthdate = date;
        this.nextIrr = next;
        this.image = image;
    }


    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets image.
     *
     * @return the image
     */
    public String getImage() {
        if("".equals(image)){
            if(family.equals("Custom")){
                image = this.getName();
            }
            if("New Cutting".equals(family)){
                image = "new_planting";
            }
        }

        return image;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return plant_name;
    }

    /**
     * Get family string.
     *
     * @return the string
     */
    public String getFamily(){return family;}

    /**
     * Sets name.
     *
     * @param title the title
     */
    public void setName(String title) {
        this.plant_name = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets irrigation.
     *
     * @return the irrigation
     */
    public int getIrrigation() {
        return irrigation;
    }

    /**
     * Gets plant birthdate.
     *
     * @return the plant birthdate
     */
    public String getPlant_birthdate() {
        return plant_birthdate;
    }


    /**
     * Get next irr string.
     *
     * @return the string
     */
    public  String getNextIrr(){
        if("".equals(nextIrr) || nextIrr==null){
            setNextIrr(irrigation);
        }
       return nextIrr;
    }


    /**
     * Set next irr.
     *
     * @param days the days
     */
    public void setNextIrr(int days){
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        Date today = new Date();
        if("".equals(nextIrr) || nextIrr==null){ //new plant
            nextIrr = f.format(today);
        }
        else{
            cal.setTime(today);
            cal.add(Calendar.DATE, days);
            nextIrr = f.format(cal.getTime());
        }
    }

    /**to manage sort by next irrigation date**/
    @Override
    public int compareTo(Object o) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        String nxt=((Plant)o).getNextIrr();

        try{
            Date date = f.parse(nxt);
            Date thisDate= f.parse(this.nextIrr);
            if(thisDate.before(date)){
                return -1;
            }
            else if(thisDate.before(date)){
                return 1;
            }
            else{
                return 0;
            }
        }catch (ParseException e){ }
        return -1;
    }
}
