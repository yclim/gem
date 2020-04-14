var rules = {
  rules: [
    {
      ruleId: "Filename Regex",
      alias: "fn-regex",
      paramDefs: [{ label: "regex", type: "regex" }],
      target: "filename"
    },
    {
      ruleId: "Filename Prefix",
      alias: "fn-prefix",
      paramDefs: [{ label: "prefix", type: "string" }],
      target: `filename`
    },
    {
      ruleId: "File Extension",
      alias: "fn-extension",
      paramDefs: [{ label: "extension", type: "string" }],
      target: `filename`
    },
    {
      ruleId: "Mime Type",
      alias: "mime-type",
      paramDefs: [{ label: "mine-type", type: "string" }],
      target: `file_mime_type`
    },
    {
      ruleId: "Raw Text Regex",
      alias: "text",
      paramDefs: [{ label: "regex", type: "string" }],
      target: `file_text`
    },
    {
      ruleId: "Tika Content Regex",
      alias: "tk-cont",
      paramDefs: [{ label: "regex", type: "regex" }],
      target: "tika_content"
    },
    {
      ruleId: "Tika Metadata",
      alias: "tk-meta",
      paramDefs: [
        { label: "key", type: "string" },
        { label: "value", type: "string" }
      ],
      target: "tika_metadata"
    },
    {
      ruleId: "CSV Header Column Value",
      alias: "csv-header-value",
      paramDefs: [{ label: "headers", type: "string_list" }],
      target: "csv"
    },
    {
      ruleId: "CSV Header Column Count",
      alias: "csv-header-count",
      paramDefs: [{ label: "# of columns", type: "int" }],
      target: "csv"
    },
    {
      ruleId: "CSV Cell Value Regex",
      alias: "csv-cell-regex",
      paramDefs: [
        { label: "row index", type: "int" },
        { label: "col index", type: "int" },
        { label: "regex", type: "regex" }
      ],
      target: "csv"
    },
    {
      ruleId: "XLS Header Column Value",
      alias: "xls-header-value",
      paramDefs: [{ label: "headers", type: "string_list" }],
      target: "xls"
    },
    {
      ruleId: "XLS Header Column Count",
      alias: "xls-header-count",
      paramDefs: [{ label: "# of columns", type: "int" }],
      target: "xls"
    },
    {
      ruleId: "XLS Cell Value Regex",
      alias: "xls-cell-regex",
      paramDefs: [
        { label: "row index", type: "int" },
        { label: "col index", type: "int" },
        { label: "regex", type: "regex" }
      ],
      target: "xls"
    }
  ]
};

var groups = {
  groups: [
    {
      groupName: "xls-1",
      rules: [
        { ruleId: "Filename Regex", label: "fn-regex-1", paramValues: [".+"] },
        {
          ruleId: "tika content regex",
          label: "tk-cont-1",
          paramValues: [".+"]
        }
      ]
    },
    {
      groupName: "xls-2",
      rules: [
        {
          ruleId: "Filename Prefix",
          label: "fn-pref-1",
          paramValues: ["sheet_"]
        },
        {
          ruleId: "Tika Content Regex",
          label: "tk-cont-1",
          paramValues: [".+"]
        }
      ]
    },
    {
      groupName: "csv-1",
      rules: [
        {
          ruleId: "Filename Prefix",
          label: "fn-pref-1",
          paramValues: ["sheet_"]
        },
        {
          ruleId: "Tika Content Regex",
          label: "tk-cont-1",
          paramValues: [".+"]
        },
        { ruleId: "filename regex", label: "fn-regex-1", paramValues: [".+"] }
      ]
    },
    {
      groupName: "txt-1",
      rules: [
        {
          ruleId: "Filename Prefix",
          label: "fn-pref-1",
          paramValues: ["text_"]
        }
      ]
    }
  ]
};
var mimeTypes = [
  "text/css",
  "text/csv",
  "application/msword",
  "image/gif",
  "text/html",
  "image/jpeg",
  "text/javascript",
  "application/json"
];

var rawTxt = [
  `Alice was beginning to get very tired of sitting by her sister on the
bank, and of having nothing to do. Once or twice she had peeped into the
book her sister was reading, but it had no pictures or conversations in
it, "and what is the use of a book," thought Alice, "without pictures or
conversations?`,

  `So she was considering in her own mind (as well as she could, for the
day made her feel very sleepy and stupid), whether the pleasure of
making a daisy-chain would be worth the trouble of getting up and
picking the daisies, when suddenly a White Rabbit with pink eyes ran
close by her.`,

  `There was nothing so very remarkable in that, nor did Alice think it so
very much out of the way to hear the Rabbit say to itself, "Oh dear! Oh
dear! I shall be too late!" But when the Rabbit actually took a watch
out of its waistcoat-pocket and looked at it and then hurried on, Alice
started to her feet, for it flashed across her mind that she had never
before seen a rabbit with either a waistcoat-pocket, or a watch to take
out of it, and, burning with curiosity, she ran across the field after
it and was just in time to see it pop down a large rabbit-hole, under
the hedge. In another moment, down went Alice after it!`,

  `The rabbit-hole went straight on like a tunnel for some way and then
dipped suddenly down, so suddenly that Alice had not a moment to think
about stopping herself before she found herself falling down what seemed
to be a very deep well.`,

  `Either the well was very deep, or she fell very slowly, for she had
plenty of time, as she went down, to look about her. First, she tried to
make out what she was coming to, but it was too dark to see anything;
then she looked at the sides of the well and noticed that they were
filled with cupboards and book-shelves; here and there she saw maps and
pictures hung upon pegs. She took down a jar from one of the shelves as
she passed. It was labeled "ORANGE MARMALADE," but, to her great
disappointment, it was empty; she did not like to drop the jar, so
managed to put it into one of the cupboards as she fell past it.`,

  `Down, down, down! Would the fall never come to an end? There was nothing
else to do, so Alice soon began talking to herself. "Dinah'll miss me
very much to-night, I should think!" (Dinah was the cat.) "I hope
they'll remember her saucer of milk at tea-time. Dinah, my dear, I wish
you were down here with me!" Alice felt that she was dozing off, when
suddenly, thump! thump! down she came upon a heap of sticks and dry
leaves, and the fall was over.`,

  `Alice was not a bit hurt, and she jumped up in a moment. She looked up,
but it was all dark overhead; before her was another long passage and
the White Rabbit was still in sight, hurrying down it. There was not a
moment to be lost. Away went Alice like the wind and was just in time to
hear it say, as it turned a corner, "Oh, my ears and whiskers, how late
it's getting!" She was close behind it when she turned the corner, but
the Rabbit was no longer to be seen.`,

  `She found herself in a long, low hall, which was lit up by a row of
lamps hanging from the roof. There were doors all 'round the hall, but
they were all locked; and when Alice had been all the way down one side
and up the other, trying every door, she walked sadly down the middle,
wondering how she was ever to get out again.`,

  `Suddenly she came upon a little table, all made of solid glass. There
was nothing on it but a tiny golden key, and Alice's first idea was that
this might belong to one of the doors of the hall; but, alas! either the
locks were too large, or the key was too small, but, at any rate, it
would not open any of them. However, on the second time 'round, she came
upon a low curtain she had not noticed before, and behind it was a
little door about fifteen inches high. She tried the little golden key
in the lock, and to her great delight, it fitted!`,

  `Alice opened the door and found that it led into a small passage, not
much larger than a rat-hole; she knelt down and looked along the passage
into the loveliest garden you ever saw. How she longed to get out of
that dark hall and wander about among those beds of bright flowers and
those cool fountains, but she could not even get her head through the
doorway. "Oh," said Alice, "how I wish I could shut up like a telescope!
I think I could, if I only knew how to begin."`,

  ,
  `Alice went back to the table, half hoping she might find another key on
it, or at any rate, a book of rules for shutting people up like
telescopes. This time she found a little bottle on it ("which certainly
was not here before," said Alice), and tied 'round the neck of the
bottle was a paper label, with the words "DRINK ME" beautifully printed
on it in large letters.`,

  `"No, I'll look first," she said, "and see whether it's marked '_poison_'
or not," for she had never forgotten that, if you drink from a bottle
marked "poison," it is almost certain to disagree with you, sooner or
later. However, this bottle was _not_ marked "poison," so Alice ventured
to taste it, and, finding it very nice (it had a sort of mixed flavor of
cherry-tart, custard, pineapple, roast turkey, toffy and hot buttered
toast), she very soon finished it off.`
];
var fileExt = ["pdf", "xls", "docx", "txt"];

var eightyNames = [
  "10 Awesome Ways to Photograph Owls",
  "7 Pictures of Liam Gallagher That We'd Rather Forget",
  "How to Increase Your Income Using Just Your Lips.",
  "21 Myths About Owls Debunked",
  "Introducing Anonymous - Who Am I And Why Should You Follow Me",
  "Vampire : Fact versus Fiction",
  "Can Owls Dance : An exploration of Memes",
  "Owls Are the New Black",
  "20 Trilby Reviews in Tweet Form",
  "From Zero to Vampire - Makeover Tips",
  "YouTube Videos",
  "How to Make Your Own Splendid Trilby for less than £5",
  "Bart Simpson's 10 Best Moments",
  "How to Attract More Splendid Subscribers",
  "A Day in the Life of Anonymous",
  "Unboxing My New Vampire Poo",
  "The Week's Top Stories About Liam Gallagher",
  "10 Things You've Always Wanted to Know About the Famous Vampire",
  "7 Unmissable YouTube Channels About Puppies",
  "10 Things Bart Simpson Can Teach Us About Puppies",
  "Mistakes That Owls Make and How to Avoid Them",
  "Album Titles",
  "Like Mars on Earth",
  "Before Hopeful Puppies",
  "Down With the Puppies",
  "Hopeful, Splendid, Intuitive",
  "Vampire Memories",
  "Puppies Dreams",
  "Mind Over Puppies",
  "The Hopeful Side of the Chord",
  "Born to Chase Owls",
  "Tunnel of Puppies",
  "Song Titles",
  "See also Song Name Generator",
  "Bridge Over Splendid Owls",
  "Truly, Madly, Splendid",
  "Liam Gallagher Dreams",
  "Chord Boogie Woogie",
  "Great Puppies of Chord",
  "Chordlife",
  "If You're Feeling Intuitive",
  "Hopeful Chord Etiquette",
  "The Chord",
  "Captain Vampire and the Puppies",
  "Essays",
  "Hopeful owls cause earthquakes. Discuss.",
  "What role do Japanese owls play in the food chain?",
  "Deciding boundaries : Owls' lips and priviledge.",
  "Is there life on Mars? Discuss with reference to eyebrows.",
  "Myths and Legends : The role of the humble vampire in shaping superstition.",
  "Bart Simpson : Is there value in analysing the impact of fictional characters on earthquakes?",
  "What can the patriarchy teach us about vampire?",
  "An analysis of intuitive puppies",
  "The Japanese Have a Unique Culture. Discuss.",
  "An brief history of splendid puppies",
  "Children's Books",
  "See also Fairytale Generator",
  "A Flock of Hopeful Owls",
  "The Splendid Vampire",
  "Why Are My Lips Growing?",
  "Mars and Other Great Places to Take Your Spaceship",
  "Owls Versus Surgeons - The Final Battle",
  "Anonymous Saves the Day",
  "The Big, Green Chord",
  "The Amazing Adventures of Anonymous",
  "King of Intuitive Puppies",
  "Enemy of the Hopeful Chord",
  "News Articles",
  "See also Headline Generator",
  "Liam Gallagher Spotted Wearing Green Trilby Again",
  "Bart Simpson To Appear In The New Star Wars Film",
  "Mars to be Visible From Earth Tonight at 1am",
  "Innapropriate Trilby Causes Earthquakes, Say Experts",
  "Controversy as Big Chord Embroilled in Health Scare",
  "Stroking Owls Prevents Serious Illness, Says Expert",
  "Shares in Puppies at All Time High",
  "A Chord Brings Down Puppies",
  "The Mystery Behind Puppies Finally Revealed",
  "You'll Never Fear Hopeful Owls Again After Reading This",
  "testfiles",
  "credentials"
];

var filenames = eightyNames.map((n, i) => {
  return {
    fileName: n + "." + fileExt[i % fileExt.length]
  };
});

var groupRules = [
  { groupName: "xls-1", files: [...filenames.slice(0, 30)] },
  { groupName: "xls-2", files: [...filenames.slice(30, 40)] },
  { groupName: "csv-1", files: [...filenames.slice(40, 75)] },
  { groupName: "txt-1", files: [...filenames.slice(75, 80)] }
];

function getFiles(groupName) {
  if (groupName === "xls-1") {
    return groupRules[0];
  } else if (groupName === "xls-2") {
    return groupRules[1];
  } else if (groupName === "csv-1") {
    return groupRules[2];
  } else {
    return groupRules[3];
  }
}

function getFilesByTypeHelper(type) {
  if (type === "All") {
    return filenames;
  } else {
    return filenames.filter(f => f.fileName.endsWith(type));
  }

  // if (type === "pdf") {
  //   return filenames.slice(0, 10);
  // } else if (type === "xls") {
  //   return filenames.slice(10, 21);
  // } else if (type === "docx") {
  //   return filenames.slice(21, 34);
  // } else if (type === "txt") {
  //   return filenames.slice(34, 50);
  // } else {
  //   return filenames.slice(50, 80);
  // }
}

const TIMEOUT = 50;
const delay = result =>
  new Promise(resolve => setTimeout(() => resolve(result), TIMEOUT));
const ruleService = {
  getRules: () => delay(JSON.parse(JSON.stringify(rules.rules))),
  getGroups: () => delay(JSON.parse(JSON.stringify(groups.groups))),
  addRuleToGroup: (gname, rid, rlabel) =>
    new Promise(resolve => {
      groups.groups
        .filter(g => g.groupName === gname)
        .map(g =>
          g.rules.push({
            ruleId: rid,
            label: rlabel,
            paramValues: ["test_param"]
          })
        );
      resolve("success");
    }),
  getGroupFiles: groupName => delay(getFiles(groupName)),
  getFileTypes: () => delay(fileExt),
  getFilesByType: type => delay(getFilesByTypeHelper(type))
};

export default ruleService;
