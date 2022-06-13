#ifndef __LUX_IMPL__
#define __LUX_IMPL__

#include "photoresistor.h"

class PhotoresistorImpl: public Photoresistor{
    public:
        PhotoresistorImpl(int photoresistorPin);
        int getLux();
    private:
        int photoresistorPin;
};

#endif
