from django.db import models
from django.conf import settings

class AnnualGoal(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    year = models.IntegerField()
    target_books = models.IntegerField()

    class Meta:
        unique_together = ('user', 'year')

    def __str__(self):
        return f"{self.user} - {self.year}"

class Author(models.Model):
    name = models.CharField(max_length=100)

    def __str__(self):
        return self.name

class Genre(models.Model):
    name = models.CharField(max_length=100, blank=True)

    def __str__(self):
        return self.name

class Book(models.Model):
    title = models.CharField(max_length=200)
    author = models.ForeignKey(Author, on_delete=models.CASCADE)
    total_pages = models.IntegerField()
    synopsis = models.TextField(blank=True)
    genres = models.ManyToManyField(Genre)

    def __str__(self):
        return self.title

class Reading(models.Model):

    STATUS_CHOICES = [
        ('reading', 'Reading'),
        ('completed', 'Completed'),
        ('abandoned', 'Abandoned'),
    ]

    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    book = models.ForeignKey(Book, on_delete=models.CASCADE)

    start_date = models.DateField(null=True, blank=True)
    end_date = models.DateField(null=True, blank=True)

    status = models.CharField(max_length=20, choices=STATUS_CHOICES)

    class Meta:
        unique_together = ('user', 'book')

    def __str__(self):
        return f"{self.user} - {self.book}"

class ReadingSession(models.Model):
    reading = models.ForeignKey(Reading, on_delete=models.CASCADE)
    date = models.DateField()
    pages_read = models.IntegerField()
    minutes_read = models.IntegerField()

    def __str__(self):
        return f"{self.date} - {self.pages_read} pages"

