#include <iostream>

using namespace std;


class Car{
    public:
    string brand;
    int year;
    
    Car(string b, int y){
        brand =b;
        year=y;
    }

    void displayInfo() {
        cout << "Brand: " << brand << endl << "Year: " << year << endl;
    }
};
int main() {
    Car car1("BMW", 2020);

    car1.displayInfo();

    return 0;
}