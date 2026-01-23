package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.drivers.transformation.ScaleStrategy;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.visitor.CommandTransformerVisitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectRunCurrentScaledDownCommandOptionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        DriverCommand command = CommandsFeature.getDriverCommandManager().getCurrentCommand();
        CommandTransformerVisitor scaler = new CommandTransformerVisitor(new ScaleStrategy(0.5, 0.5));
        command.accept(scaler);
        DriverCommand command_scaled = scaler.getTransformedCommand();
        CommandsFeature.getDriverCommandManager().setCurrentCommand(command_scaled);
    }
}
