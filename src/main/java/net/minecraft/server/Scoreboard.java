package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class Scoreboard {

    private final Map<String, ScoreboardObjective> objectivesByName = Maps.newHashMap();
    private final Map<IScoreboardCriteria, List<ScoreboardObjective>> objectivesByCriteria = Maps.newHashMap();
    private final Map<String, Map<ScoreboardObjective, ScoreboardScore>> playerScores = Maps.newHashMap();
    private final ScoreboardObjective[] displaySlots = new ScoreboardObjective[19];
    private final Map<String, ScoreboardTeam> teamsByName = Maps.newHashMap();
    private final Map<String, ScoreboardTeam> teamsByPlayer = Maps.newHashMap();
    private static String[] g;

    public Scoreboard() {}

    public ScoreboardObjective c(String s) {
        return (ScoreboardObjective) this.objectivesByName.get(s);
    }

    @Nullable
    public ScoreboardObjective getObjective(@Nullable String s) {
        return (ScoreboardObjective) this.objectivesByName.get(s);
    }

    public ScoreboardObjective registerObjective(String s, IScoreboardCriteria iscoreboardcriteria, IChatBaseComponent ichatbasecomponent, IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay) {
        if (s.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + s + "' is too long!");
        } else if (this.objectivesByName.containsKey(s)) {
            throw new IllegalArgumentException("An objective with the name '" + s + "' already exists!");
        } else {
            ScoreboardObjective scoreboardobjective = new ScoreboardObjective(this, s, iscoreboardcriteria, ichatbasecomponent, iscoreboardcriteria_enumscoreboardhealthdisplay);

            ((List) this.objectivesByCriteria.computeIfAbsent(iscoreboardcriteria, (iscoreboardcriteria1) -> {
                return Lists.newArrayList();
            })).add(scoreboardobjective);
            this.objectivesByName.put(s, scoreboardobjective);
            this.handleObjectiveAdded(scoreboardobjective);
            return scoreboardobjective;
        }
    }

    public final void getObjectivesForCriteria(IScoreboardCriteria iscoreboardcriteria, String s, Consumer<ScoreboardScore> consumer) {
        ((List) this.objectivesByCriteria.getOrDefault(iscoreboardcriteria, Collections.emptyList())).forEach((scoreboardobjective) -> {
            consumer.accept(this.getPlayerScoreForObjective(s, (ScoreboardObjective) scoreboardobjective)); // Akarin - fixes decompile error
        });
    }

    public boolean b(String s, ScoreboardObjective scoreboardobjective) {
        Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.get(s);

        if (map == null) {
            return false;
        } else {
            ScoreboardScore scoreboardscore = (ScoreboardScore) map.get(scoreboardobjective);

            return scoreboardscore != null;
        }
    }

    public ScoreboardScore getPlayerScoreForObjective(String s, ScoreboardObjective scoreboardobjective) {
        if (s.length() > 40) {
            throw new IllegalArgumentException("The player name '" + s + "' is too long!");
        } else {
            Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.computeIfAbsent(s, (s1) -> {
                return Maps.newHashMap();
            });

            return (ScoreboardScore) map.computeIfAbsent(scoreboardobjective, (scoreboardobjective1) -> {
                ScoreboardScore scoreboardscore = new ScoreboardScore(this, scoreboardobjective1, s);

                scoreboardscore.setScore(0);
                return scoreboardscore;
            });
        }
    }

    public Collection<ScoreboardScore> getScoresForObjective(ScoreboardObjective scoreboardobjective) {
        List<ScoreboardScore> list = Lists.newArrayList();
        Iterator iterator = this.playerScores.values().iterator();

        while (iterator.hasNext()) {
            Map<ScoreboardObjective, ScoreboardScore> map = (Map) iterator.next();
            ScoreboardScore scoreboardscore = (ScoreboardScore) map.get(scoreboardobjective);

            if (scoreboardscore != null) {
                list.add(scoreboardscore);
            }
        }

        Collections.sort(list, ScoreboardScore.a);
        return list;
    }

    public Collection<ScoreboardObjective> getObjectives() {
        return this.objectivesByName.values();
    }

    public Collection<String> d() {
        return this.objectivesByName.keySet();
    }

    public Collection<String> getPlayers() {
        return Lists.newArrayList(this.playerScores.keySet());
    }
    // Akarin start
    public boolean containsPlayer(String playerName) {
        return this.playerScores.containsKey(playerName);
    }
    // Akarin end

    public void resetPlayerScores(String s, @Nullable ScoreboardObjective scoreboardobjective) {
        Map map;

        if (scoreboardobjective == null) {
            map = (Map) this.playerScores.remove(s);
            if (map != null) {
                this.handlePlayerRemoved(s);
            }
        } else {
            map = (Map) this.playerScores.get(s);
            if (map != null) {
                ScoreboardScore scoreboardscore = (ScoreboardScore) map.remove(scoreboardobjective);

                if (map.size() < 1) {
                    Map<ScoreboardObjective, ScoreboardScore> map1 = (Map) this.playerScores.remove(s);

                    if (map1 != null) {
                        this.handlePlayerRemoved(s);
                    }
                } else if (scoreboardscore != null) {
                    this.a(s, scoreboardobjective);
                }
            }
        }

    }

    public Map<ScoreboardObjective, ScoreboardScore> getPlayerObjectives(String s) {
        Map<ScoreboardObjective, ScoreboardScore> map = (Map) this.playerScores.get(s);

        if (map == null) {
            map = Maps.newHashMap();
        }

        return (Map) map;
    }

    public void unregisterObjective(ScoreboardObjective scoreboardobjective) {
        this.objectivesByName.remove(scoreboardobjective.getName());

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                this.setDisplaySlot(i, (ScoreboardObjective) null);
            }
        }

        List<ScoreboardObjective> list = (List) this.objectivesByCriteria.get(scoreboardobjective.getCriteria());

        if (list != null) {
            list.remove(scoreboardobjective);
        }

        Iterator iterator = this.playerScores.values().iterator();

        while (iterator.hasNext()) {
            Map<ScoreboardObjective, ScoreboardScore> map = (Map) iterator.next();

            map.remove(scoreboardobjective);
        }

        this.handleObjectiveRemoved(scoreboardobjective);
    }

    public void setDisplaySlot(int i, @Nullable ScoreboardObjective scoreboardobjective) {
        this.displaySlots[i] = scoreboardobjective;
    }

    @Nullable
    public ScoreboardObjective getObjectiveForSlot(int i) {
        return this.displaySlots[i];
    }

    public ScoreboardTeam getTeam(String s) {
        return (ScoreboardTeam) this.teamsByName.get(s);
    }

    public ScoreboardTeam createTeam(String s) {
        if (s.length() > 16) {
            throw new IllegalArgumentException("The team name '" + s + "' is too long!");
        } else {
            ScoreboardTeam scoreboardteam = this.getTeam(s);

            if (scoreboardteam != null) {
                throw new IllegalArgumentException("A team with the name '" + s + "' already exists!");
            } else {
                scoreboardteam = new ScoreboardTeam(this, s);
                this.teamsByName.put(s, scoreboardteam);
                this.handleTeamAdded(scoreboardteam);
                return scoreboardteam;
            }
        }
    }

    public void removeTeam(ScoreboardTeam scoreboardteam) {
        this.teamsByName.remove(scoreboardteam.getName());
        Iterator iterator = scoreboardteam.getPlayerNameSet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.teamsByPlayer.remove(s);
        }

        this.handleTeamRemoved(scoreboardteam);
    }

    public boolean addPlayerToTeam(String s, ScoreboardTeam scoreboardteam) {
        if (s.length() > 40) {
            throw new IllegalArgumentException("The player name '" + s + "' is too long!");
        } else {
            if (this.getPlayerTeam(s) != null) {
                this.removePlayerFromTeam(s);
            }

            this.teamsByPlayer.put(s, scoreboardteam);
            return scoreboardteam.getPlayerNameSet().add(s);
        }
    }

    public boolean removePlayerFromTeam(String s) {
        ScoreboardTeam scoreboardteam = this.getPlayerTeam(s);

        if (scoreboardteam != null) {
            this.removePlayerFromTeam(s, scoreboardteam);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        if (this.getPlayerTeam(s) != scoreboardteam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + scoreboardteam.getName() + "'.");
        } else {
            this.teamsByPlayer.remove(s);
            scoreboardteam.getPlayerNameSet().remove(s);
        }
    }

    public Collection<String> f() {
        return this.teamsByName.keySet();
    }

    public Collection<ScoreboardTeam> getTeams() {
        return this.teamsByName.values();
    }

    @Nullable
    public ScoreboardTeam getPlayerTeam(String s) {
        return (ScoreboardTeam) this.teamsByPlayer.get(s);
    }

    public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {}

    public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {}

    public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {}

    public void handleScoreChanged(ScoreboardScore scoreboardscore) {}

    public void handlePlayerRemoved(String s) {}

    public void a(String s, ScoreboardObjective scoreboardobjective) {}

    public void handleTeamAdded(ScoreboardTeam scoreboardteam) {}

    public void handleTeamChanged(ScoreboardTeam scoreboardteam) {}

    public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {}

    public static String getSlotName(int i) {
        switch (i) {
        case 0:
            return "list";
        case 1:
            return "sidebar";
        case 2:
            return "belowName";
        default:
            if (i >= 3 && i <= 18) {
                EnumChatFormat enumchatformat = EnumChatFormat.a(i - 3);

                if (enumchatformat != null && enumchatformat != EnumChatFormat.RESET) {
                    return "sidebar.team." + enumchatformat.g();
                }
            }

            return null;
        }
    }

    public static int getSlotForName(String s) {
        if ("list".equalsIgnoreCase(s)) {
            return 0;
        } else if ("sidebar".equalsIgnoreCase(s)) {
            return 1;
        } else if ("belowName".equalsIgnoreCase(s)) {
            return 2;
        } else {
            if (s.startsWith("sidebar.team.")) {
                String s1 = s.substring("sidebar.team.".length());
                EnumChatFormat enumchatformat = EnumChatFormat.c(s1);

                if (enumchatformat != null && enumchatformat.b() >= 0) {
                    return enumchatformat.b() + 3;
                }
            }

            return -1;
        }
    }

    public static String[] h() {
        if (Scoreboard.g == null) {
            Scoreboard.g = new String[19];

            for (int i = 0; i < 19; ++i) {
                Scoreboard.g[i] = getSlotName(i);
            }
        }

        return Scoreboard.g;
    }

    public void a(Entity entity) {
        if (entity != null && !(entity instanceof EntityHuman) && !entity.isAlive()) {
            String s = entity.bu();

            this.resetPlayerScores(s, (ScoreboardObjective) null);
            this.removePlayerFromTeam(s);
        }
    }

    protected NBTTagList i() {
        NBTTagList nbttaglist = new NBTTagList();

        this.playerScores.values().stream().map(Map::values).forEach((collection) -> {
            collection.stream().filter((scoreboardscore) -> {
                return scoreboardscore.getObjective() != null;
            }).forEach((scoreboardscore) -> {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setString("Name", scoreboardscore.getPlayerName());
                nbttagcompound.setString("Objective", scoreboardscore.getObjective().getName());
                nbttagcompound.setInt("Score", scoreboardscore.getScore());
                nbttagcompound.setBoolean("Locked", scoreboardscore.g());
                nbttaglist.add((NBTBase) nbttagcompound);
            });
        });
        return nbttaglist;
    }

    protected void a(NBTTagList nbttaglist) {
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            ScoreboardObjective scoreboardobjective = this.c(nbttagcompound.getString("Objective"));
            String s = nbttagcompound.getString("Name");

            if (s.length() > 40) {
                s = s.substring(0, 40);
            }

            ScoreboardScore scoreboardscore = this.getPlayerScoreForObjective(s, scoreboardobjective);

            scoreboardscore.setScore(nbttagcompound.getInt("Score"));
            if (nbttagcompound.hasKey("Locked")) {
                scoreboardscore.a(nbttagcompound.getBoolean("Locked"));
            }
        }

    }
}
