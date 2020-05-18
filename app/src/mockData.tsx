const fileExt = ["pdf", "xls", "docx", "txt"];

const eightyNames = [
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

const filenames = eightyNames.map((n, i) => {
  return {
    filename: n + "." + fileExt[i % fileExt.length]
  };
});

export const fileExtractCounts = filenames.map(f => {
  return {
    filename: f.filename,
    count: 0,
    absolutePath: "",
    extractedData: []
  };
});

export function makeStr(n: number) {
  let result = "";
  const characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  const charactersLength = characters.length;
  for (let i = 0; i < length; i++) {
    result += characters.charAt(Math.floor(Math.random() * charactersLength));
  }
  return result;
}

export default filenames;
