package thread;

import static DEBUG.Dbg.msg;

// import BasePlayer.BasePlayer;
import BasePlayer.HumanPlayer;
import BasePlayer.Indirect;
import main.Game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
// import java.util.List;
import java.util.Stack;

public class GameKeyListener implements KeyListener
{
    private Stack<Integer> pressStack = new Stack<>();
	private Game g = null;
	private HumanPlayer player = null;
    //private List<SuperObject> playerList;

	public GameKeyListener(Game g) {this.g = g;}
	
    @Override
    public void keyPressed(KeyEvent e)
	{
		// msg("?????????");
        int code = e.getKeyCode();
        if (player.isAlive())
		{
            switch (code)
			{
                case 32: // 空格，放置泡泡
					g.commandQueue.add(() -> {player.placeBomb();});
					break;
				case 37: case 38: case 39: case 40: // 上下左右
                // case 87: case 65: case 68: case 83: // wasd
					if (!pressStack.contains(code)) pressStack.push(code);
					if (pressStack.peek() == code)
						g.commandQueue.add(() -> {player.move(Indirect.codeToIndirect(code));});
						// player.setIndirect();
					break;
                    // if (player.isKeepAttack()) {
                    //     player.setAttack(false);
                    // } else {
                    //     if(!player.isDying()){
                    //         player.setAttack(true);
                    //         player.setKeepAttack(true);
                    //     }
                    // }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
	{
    	// HumanPlayer player = g.getInfoPlayer(); // 想取出玩家指针，这句话咋写啊，不会
        int code = e.getKeyCode();
        if (player.isAlive())
		{
            switch (code)
			{
                case 32:
					g.commandQueue.add(() -> {player.release();});
                    break; //
                    // player.setKeepAttack(false);
                    // player.setAttack(false);
                case 37: case 38: case 39: case 40:
                case 87: case 65: case 68: case 83:
					pressStack.remove(Integer.valueOf(code));
                    break;
                    // if(pressStack.peek() != code){ // 栈顶
                    //     pressStack.remove(Integer.valueOf(code)); //integer.valueof构造函数
                    // }else {
                    //     pressStack.pop();
                    //     if(pressStack.size() == 0){
                    //     	player.setIndirect(Indirect.STOP);
                    //     }else {
                    //     	player.setIndirect(Indirect.codeToIndirect(pressStack.peek()));
                    //     }
                    // }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

	public void setPlayer(HumanPlayer player) {this.player = player;}
    public void clearPlayer(){this.player = null; pressStack.clear();}
}
