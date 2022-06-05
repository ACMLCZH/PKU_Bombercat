package thread;

/*
import controller.GameController;
import controller.ObjectController;
import model.gamecharacter.MoveType;
import model.gamecharacter.Player;
import model.gameobject.SuperObject;
*/
import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import BasePlayer.Indirect;
import main.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Stack;

public class GameKeyListener implements KeyListener {

    private Stack<Integer> pressStack = new Stack<>();
    //private List<SuperObject> playerList;

    @Override
    public void keyPressed(KeyEvent e) {
    	HumanPlayer player=Game.getInfoPlayer(); // ��ȡ�����ָ�룬��仰զд��������
        int code = e.getKeyCode();
        if(player.isAlive()){
            switch (code){
                case 32: //�ո�
                    if(player.isKeepAttack()){
                        player.setAttack(false);
                    }else {
                        if(!player.isDying()){
                            player.setAttack(true);
                            player.setKeepAttack(true);
                        }
                    }
                    break; //��������
                /*
                case 37: // ��������
                case 38:
                case 39:
                case 40:
                */
                case 87:
                case 65:
                case 68:
                case 83: // wasd
                    if(!pressStack.contains(code)){
                        pressStack.push(code);
                        player.setIndirect(Indirect.codeToIndirect(code));
                    }
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	HumanPlayer player=Game.getInfoPlayer(); // ��ȡ�����ָ�룬��仰զд��������
        int code = e.getKeyCode();
        if(player.isAlive()){
            switch (code){
                case 32:
                    player.setKeepAttack(false);
                    player.setAttack(false);
                    break; //
                case 37:
                case 38:
                case 39:
                case 40:
                case 87:
                case 65:
                case 68:
                case 83:
                    if(pressStack.peek()!=code){ // ջ��
                        pressStack.remove(Integer.valueOf(code)); //integer.valueof���캯��
                    }else {
                        pressStack.pop();
                        if(pressStack.size() == 0){
                        	player.setIndirect(Indirect.STOP);
                        }else {
                        	player.setIndirect(Indirect.codeToIndirect(pressStack.peek()));
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void clearPressStack(){
        pressStack.clear();
    }
}
