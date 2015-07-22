#寫字機器人

![openlabtaipei hackpad com_6xtqjnynliv_p 292086_1432106453005_2015-05-20 15-12-35](https://cloud.githubusercontent.com/assets/12403337/8766051/64cc5fbc-2e5c-11e5-953e-dc52c89521c9.png)

操作的畫面如上圖所示，用手指頭在螢幕上畫圖，然後命令小車在地面上按照這個圖形行走。
流程：  
1. 以藍牙和小車連線  
2. 在螢幕上畫圖  
3. 確認圖形，用程式算出移動的方法  
    程式會算出一個接著一個的點，在每一個點上面會有一個指令告訴小車-轉幾度、向前走多遠，然後再轉幾度、向前走多遠...  
4. 然後送出第一個點的指令，當收到小車回覆要求下一個點的指令時，就送出下一個的指令(並不一次送出所有的點)  
5. 接收小車回覆的進度，更新畫面顯示小車已經走到哪裡  

用Android手機透過藍芽與Arduino控制的小車連線，讓小車以移動自身位置的方式畫出手機上面的圖形。  
圖形可以運用APP以手指頭在螢幕上畫出，或是使用 APP中內建的特殊圖形(在影片中是個玫瑰線)。    
實際運作時，Arduino小車子的控制並不是很精確，所以無法精確畫出所望的圖形。  
[實際運作示範](https://www.youtube.com/watch?v=oj6462PY5eQ)


小車的 Arduino程式
```cpp

int pinRightMode1 = 12;
int pinRightMode2 = 13;
int pinLeftMode1 = 10;
int pinLeftMode2 = 11;

unsigned leftWheelCount = 0;
unsigned rightWheelCount = 0;

#define STOP 0
#define FORWARD  1
#define BACKWARD  -1




#define CMD_STOP 0
#define CMD_GO_FORWARD 1
#define CMD_TURN_LEFT 2
#define CMD_TURN_RIGHT 3

#define MM_PER_COUNTER_VAL 11
#define WHEEL_CIRUM_MM  848.0

boolean cmdCompleteFlag = true;


int pinLeftSpeed = 5;
int pinRightSpeed = 6;

int channelValue[9];
int *command = &channelValue[0];
int *cmd_value = &channelValue[1];



String message;

void setup() {

  pinMode (2, INPUT_PULLUP);
  pinMode (3, INPUT_PULLUP);

  pinMode(pinLeftMode1, OUTPUT);
  pinMode(pinLeftMode2, OUTPUT);
  pinMode(pinRightMode1, OUTPUT);
  pinMode(pinRightMode2, OUTPUT);

  pinMode(pinLeftSpeed, OUTPUT);
  pinMode(pinLeftSpeed, OUTPUT);

  attachInterrupt(0, countLeft, RISING);
  attachInterrupt(1, countRight, RISING);

  Serial.begin(57600);

}


void loop() {

  while (Serial.available())
  {

    char c = Serial.read();
    if (c != ';') {
      message += c;

    } else {
      if (message != "")
      {
        boolean checkSumIsValid = parseChannelValues(message);
        if (checkSumIsValid) {
          cmdControl();

        } else {
         //Serial.println(message + " droped!;");
        }
         message = "";
      }
    }
  }
}


void cmdControl() {
  int cmd = *command;
  int value = *cmd_value;

  switch (cmd) {
    case CMD_GO_FORWARD:
      goForward(value);
      Serial.println("CURRENT_ACTION_COMPLETED;");
      break;
    case CMD_TURN_LEFT:
      turnLeft(value);
      Serial.println("ASK_NEXT_ACTION_FROM_VEHICLE;");
      break;
    case CMD_TURN_RIGHT:
      turnRight(value);
      Serial.println("ASK_NEXT_ACTION_FROM_VEHICLE;");
      break;

    default:
      Serial.println("DEFAULT;");
      stop();
      break;
  }

}



void stop() {
  setLeftWheelStop();
  setRightWheelStop();
}

void setLeftWheelGoForward(int speed) {
  digitalWrite(pinLeftMode1, LOW);
  digitalWrite(pinLeftMode2, HIGH);
  analogWrite(pinLeftSpeed, speed);

}

void setRightWheelGoForward(int speed) {
  digitalWrite(pinRightMode1, LOW);
  digitalWrite(pinRightMode2, HIGH);
  analogWrite(pinRightSpeed, speed);
}


void setLeftWheelGoBackward(int speed) {
  digitalWrite(pinLeftMode1, HIGH);
  digitalWrite(pinLeftMode2, LOW);
  analogWrite(pinLeftSpeed, speed);

}

void setRightWheelGoBackward(int speed) {
  digitalWrite(pinRightMode1, HIGH);
  digitalWrite(pinRightMode2, LOW);
  analogWrite(pinRightSpeed, speed);
}

void setLeftWheelStop() {
  digitalWrite(pinLeftMode1, HIGH);
  digitalWrite(pinLeftMode2, HIGH);
  analogWrite(pinLeftSpeed, 0);

}

void setRightWheelStop() {
  digitalWrite(pinRightMode1, HIGH);
  digitalWrite(pinRightMode2, HIGH);
  analogWrite(pinRightSpeed, 0);
}

boolean parseChannelValues(String message) {
  int channelCounter = 0;
  int checkSum = 0;
  for (int i = 0; i < 27; i += 3) {
    int value = 0;
    value += (message[i] - 48) * 100;
    value += (message[i + 1] - 48) * 10;
    value += (message[i + 2] - 48);
    channelValue[channelCounter++] = value;
  }
  int checkSumValue  = 0;
  for (int i = 0; i < 24; i++) {
    checkSumValue += (message[i] - 48);
  }

  int checkSumField = channelValue[8];
  if (checkSumValue !=  checkSumField) {
    return false;
  }
  return true;
}

void countLeft() {
  leftWheelCount += 1;
}


void countRight() {
  rightWheelCount += 1;
}

void resetCounters() {
  leftWheelCount = 0;
  rightWheelCount = 0;
}

void goForward(int mm) {

  resetCounters();
  int distConut = mm / MM_PER_COUNTER_VAL / 2;
  while (rightWheelCount < distConut) {
    setLeftWheelGoForward(150);
    setRightWheelGoForward(150);
  }
  stop();
}


void turnRight(double degree) {
  resetCounters();

  double whillDistance = degree * WHEEL_CIRUM_MM / 360.0;
  double distCount = whillDistance / (MM_PER_COUNTER_VAL+14);

  while (leftWheelCount < distCount) {
    setRightWheelGoForward(60);
    setLeftWheelGoForward(160);
  }

  stop();

}

void turnLeft(double degree) {
  resetCounters();

  double whillDistance = degree * WHEEL_CIRUM_MM / 360.0;
  double distCount = whillDistance / (MM_PER_COUNTER_VAL+14);

  while (rightWheelCount < distCount) {
    setLeftWheelGoForward(60);
    setRightWheelGoForward(160);
  }

  stop();

}


```