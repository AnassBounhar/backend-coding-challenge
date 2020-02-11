package backend.challenge.pojo;

import java.util.List;

public class Language {
	
	private String languageid;
	private int usecount;
	private List repos;
	
	public void addRepo(String repo) {
		repos.add(repo);
	}
	
	public void anotherUse() {
		usecount++;
	}
	
	public Language(String languageid, int usecount, List repos) {
		super();
		this.languageid = languageid;
		this.usecount = usecount;
		this.repos = repos;
	}
	public String getLanguageid() {
		return languageid;
	}
	public void setLanguageid(String languageid) {
		this.languageid = languageid;
	}
	public int getUsecount() {
		return usecount;
	}
	public void setUsecount(int usecount) {
		this.usecount = usecount;
	}
	public List getRepos() {
		return repos;
	}
	public void setRepos(List repos) {
		this.repos = repos;
	}

	@Override
	public String toString() {
		return "|\t" + languageid + "\t|\t" + usecount + "\t|\t" + repos + "|";
	}

}
