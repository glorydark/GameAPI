package gameapi.commands.data;

import gameapi.commands.data.entry.OperationEntry;
import lombok.Builder;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
@Builder
@ToString
public class WorldEditOperation {

    public List<OperationEntry> changedBlockEntries = new ArrayList<>();

    public boolean sudo() {
        for (OperationEntry changedBlockEntry : changedBlockEntries) {
            changedBlockEntry.sudo();
        }
        return true;
    }
}
