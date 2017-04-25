using System;
using System.IO;


namespace VaultCmdLineClient
{
	// NOTE : we shouldn't be writing our own XML writer as
	// NOTE : this will surely lead to nasty bugs that
	// NOTE : we never expected.
	// NOTE : i suspect that if we ever have any more bugs
	// NOTE : logged against the xml output, we should just
	// NOTE : ditch this and move to somebody else's
	// NOTE : (debugged) xml engine
	public class XMLOutputWriter
	{
		private TextWriter tw;
		private Element top;
		private Element cur;

		public XMLOutputWriter(TextWriter output)
		{
			tw = output;
		}

		public void Begin(string name)
		{
			Element e = new Element(name, cur);

			if (cur == null)
			{
				cur = e;
				top = e;
			}
			else
			{
				cur.AddChild(e);
				cur = e;
			}
		}

		public void End()
		{
			cur = cur.parent;
			if (cur == null)
			{
				WriteXML();

				top = null;
			}
		}

		public string CurrentElement
		{
			get
			{
				if (cur == null)
				{
					return null;
				}

				return cur.name;
			}
		}

		public void AddPair(string name, string val)
		{
			cur.AddPair(name, val);
		}

		public void AddPair(string name, int val)
		{
			cur.AddPair(name, val.ToString());
		}

		public void AddPair(string name, long val)
		{
			cur.AddPair(name, val.ToString());
		}

		public void AddPair(string name, bool val)
		{
			cur.AddPair(name, val ? "yes" : "no");
		}

		private void Indent(int depth)
		{
			for (int i=0; i<depth; i++)
			{
				tw.Write(" ");
			}
		}

		private void WriteTag(Element e, int depth)
		{
			Indent(depth);
			tw.Write("<{0}", e.name);

			foreach (Pair p in e.pairs)
			{
				if(
					p.strName.IndexOf('"') >= 0 ||
					p.strName.IndexOf('=') >= 0 ||
					p.strName.IndexOf('<') >= 0 ||
					p.strName.IndexOf('>') >= 0 ||
					p.strName.IndexOf('&') >= 0
					)
				{
					throw new Exception("xml tag name contains invalid character");
				}

				string strValueSafe = (p.strValue != null) ? p.strValue : string.Empty;

				strValueSafe = strValueSafe.Replace("&", "&amp;");
				strValueSafe = strValueSafe.Replace("<", "&lt;");
				strValueSafe = strValueSafe.Replace(">", "&gt;");
				strValueSafe = strValueSafe.Replace("\"", "&quot;");
				strValueSafe = strValueSafe.Replace("“", "&quot;"); 
				strValueSafe = strValueSafe.Replace("”", "&quot;"); 

				tw.Write(" {0}=\"{1}\"", p.strName, strValueSafe);
			}

			if (!e.isEmpty())
			{
				tw.WriteLine(">");
				foreach (string s in e.content)
				{
					tw.WriteLine(s);
				}

				foreach (Element sub in e.children)
				{
					WriteTag(sub, depth+1);
				}
				Indent(depth);
				tw.WriteLine("</{0}>", e.name);
			}
			else
			{
				tw.WriteLine(" />");
			}
		}

		private void WriteXML()
		{
			WriteTag(top, 0);
		}

		public void WriteUserMessage(string s)
		{
			string strSafe = s;

			strSafe.Replace("-->", "-- >");

			tw.WriteLine("<!--  {0}  -->", strSafe);
		}

		public void WriteContent(string s)
		{
			string strSafe = s;

			strSafe = strSafe.Replace("&", "&amp;");
			strSafe = strSafe.Replace("<", "&lt;");
			strSafe = strSafe.Replace(">", "&gt;");

			this.cur.content.Add(strSafe);
		}

		public void WriteContent()
		{
			this.cur.content.Add("");
		}
	}

}
