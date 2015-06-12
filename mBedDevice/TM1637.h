#ifndef TM1637_h
#define TM1637_h

//#include "stdint.h"
#include "mbed.h"

//************definitions for TM1637*********************
#define ADDR_AUTO  0x40
#define ADDR_FIXED 0x44

#define STARTADDR  0xc0 
/**** definitions for the clock point of the digit tube *******/
#define POINT_ON   1
#define POINT_OFF  0
/**************definitions for brightness***********************/
#define  BRIGHT_DARKEST 0
#define  BRIGHT_TYPICAL 2
#define  BRIGHTEST      7

class TM1637 {
  public:
    TM1637(DigitalOut *  Clk, DigitalInOut *  Data);
  
    uint8_t Cmd_SetData;
    uint8_t Cmd_SetAddr;
    uint8_t Cmd_DispCtrl;
    bool _PointFlag;     //_PointFlag=1:the clock point on
    void init(void);        //To clear the display
    void writeByte(int8_t wr_data);//write 8bit data to tm1637
    void start(void);//send start bits
    void stop(void); //send stop bits
    void display4(int8_t *DispData);
    void display(uint8_t BitAddr,int8_t DispData);
    void clearDisplay(void);
    void set(uint8_t = BRIGHT_TYPICAL,uint8_t = 0x40,uint8_t = 0xc0);//To take effect the next time it displays.
    void point(bool PointFlag);//whether to light the clock point ":".To take effect the next time it displays.
    void coding(int8_t DispData[]); 
    int8_t coding(int8_t DispData); 
  private:
    DigitalOut * Clkpin;
    DigitalInOut * Datapin;
};
#endif