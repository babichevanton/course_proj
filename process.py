def strip(s, k):
	return s[0:len(s) - k]

fres = open("res.txt")
fgt = open("data/data.txt")

results = list()
cur = list()
for line in fres.readlines():
	if line == "\n":
		results.append(cur)
		cur = list()
	else:
		sp = line.split(' ', 1)
		sp[1] = strip(sp[1], 1)
		cur.append(sp)

attributes = ("Color", "Screen Size", "RAM", "Hard Drive", "Brand Name", "Series")

gt = list()
txt = fgt.readlines()
for i in range(len(txt)):
	txt[i] = strip(txt[i], 1)
	#print txt[i]
txt.append("")

for i in range(50):
	cur = list()
	cur.append(txt[i * 9 + 7])
	cur.append(txt[i * 9 + 2])
	cur.append(txt[i * 9 + 5])
	cur.append(txt[i * 9 + 6])
	cur.append(txt[i * 9 + 0])
	cur.append(txt[i * 9 + 1])
	gt.append(cur)

pa = 0
ra = 0
fa = 0

for ati in range(len(attributes)):
	TP = 0
	FP = 0
	TN = 0
	FN = 0

	for posti in range(50):
		good = gt[posti][ati]
		for pair in results[posti]:
			if pair[1] == attributes[ati]:
				TP += pair[0] == good
				FP += pair[0] != good
				#print pair, [good], attributes[ati]
			else:
				TN += pair[0] != good
				FN += pair[0] == good
	#print TP, FP, TN, FN
	p = 1.0 * TP / (TP + FP)
	r = 1.0 * TP / (TP + FN)
	f = 2.0 * p * r / (p + r)

	pa += p
	ra += r
	fa += f
	print p, r, f, attributes[ati]

pa /= 1.0 * len(attributes)
ra /= 1.0 * len(attributes)
fa /= 1.0 * len(attributes)

print ""
print pa, ra, fa, "Average"